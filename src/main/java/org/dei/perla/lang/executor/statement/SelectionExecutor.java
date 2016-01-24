package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.executor.buffer.UnreleasedViewException;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.Select;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.WindowSize;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Executor for the {@link SelectionStatement} class
 *
 * @author Guido Rota 22/04/15.
 */
public final class SelectionExecutor {

    private static final String DROPPING_RECORD_ERROR =
            "Dropping record, previous selection is still in progress. Reduce" +
                    " frequency in EVERY clause to avoid this error";

    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int STOPPED = 2;

    private static final ScheduledExecutorService timer =
            Executors.newScheduledThreadPool(5);
    private static final ExecutorService exec =
            Executors.newCachedThreadPool();

    private final Select select;
    private final WindowSize every;
    private final QueryHandler<Select, Object[]> handler;

    private final Lock lk = new ReentrantLock();
    private int status = READY;

    private final Buffer buffer;

    private int everyCount;
    private ScheduledFuture<?> everyThread;

    public SelectionExecutor(
            Select select,
            List<Attribute> selAtts,
            QueryHandler<Select, Object[]> handler) {
        this.select = select;
        every = select.getEvery();
        this.handler = handler;
        buffer = new ArrayBuffer(selAtts);
    }

    public void start() {
        lk.lock();
        try {
            if (status == RUNNING) {
                return;
            } else if (status == STOPPED) {
                throw new IllegalStateException(
                        "Cannot restart SelectionExecutor");
            }
            status = RUNNING;
            startEvery();
        } finally {
            lk.unlock();
        }
    }

    private void startEvery() {
        switch (every.getType()) {
            case TIME:
                long ms = every.getDuration().toMillis();
                everyThread = timer.scheduleAtFixedRate(
                        new EveryRunner(),
                        ms,
                        ms,
                        TimeUnit.MILLISECONDS);
                break;
            case SAMPLE:
                everyCount = every.getSamples();
                break;
            default:
                throw new RuntimeException(
                        "Unknown window size type " + every.getType());
        }
    }

    public void stop() {
        lk.lock();
        try {
            if (status == STOPPED) {
                return;
            } else if (status == READY) {
                throw new IllegalStateException(
                        "SelectionExecutor has not been started");
            }
            if (everyThread != null) {
                everyThread.cancel(true);
                everyThread = null;
            }
            status = STOPPED;
        } finally {
            lk.unlock();
        }
    }

    public boolean isRunning() {
        lk.lock();
        try {
            return status == RUNNING;
        } finally {
            lk.unlock();
        }
    }


    /**
     * Time-base EVERY runner
     */
    private final class EveryRunner implements Runnable {

        public void run() {
            lk.lock();
            try {
                if (status != RUNNING) {
                    return;
                }
                BufferView view = buffer.createView();
                exec.execute(new SelectionRunner(view));
            } catch (UnreleasedViewException e) {
                QueryException qe = new QueryException(
                        DROPPING_RECORD_ERROR,
                        e
                );
                handler.error(select, qe);
                stop();
            } finally {
                lk.unlock();
            }
        }

    }

    protected void error(Sampling source, Throwable cause) {
        lk.lock();
        try {
            handler.error(select, cause);
            stop();
        } finally {
            lk.unlock();
        }
    }

    protected void data(Sampling source, Object[] sample) {
        lk.lock();
        try {
            buffer.add(sample);
            if (every.getType() == WindowSize.WindowType.SAMPLE) {
                triggerCountSampling();
            }
        } finally {
            lk.unlock();
        }
    }

    private void triggerCountSampling() {
        everyCount--;
        if (everyCount != 0) {
            return;
        }

        try {
            everyCount = every.getSamples();
            BufferView view = buffer.createView();
            exec.execute(new SelectionRunner(view));
        } catch(UnreleasedViewException e) {
            QueryException qe = new QueryException(
                    DROPPING_RECORD_ERROR,
                    e
            );
            handler.error(select, qe);
            stop();
        }
    }


    /**
     * Selection runner
     */
    private final class SelectionRunner implements Runnable {

        private final BufferView view;

        public SelectionRunner(BufferView view) {
            this.view = view;
        }

        public void run() {
            List<Object[]> res = select.select(view);
            view.release();
            lk.lock();
            try {
                if (status != RUNNING) {
                    return;
                }

                res.forEach((r) -> handler.data(select, r));
            } finally {
                lk.unlock();
            }
        }

    }

}
