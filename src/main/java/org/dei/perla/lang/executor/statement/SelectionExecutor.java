package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.SelectionStatementHandler;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.executor.buffer.UnreleasedViewException;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.Sampling;
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

    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int STOPPED = 2;

    private static final ScheduledExecutorService timer =
            Executors.newScheduledThreadPool(5);
    private static final ExecutorService exec =
            Executors.newCachedThreadPool();

    private final Fpc fpc;
    private final SelectionStatement query;
    private final List<Attribute> selAtts;
    private final Expression where;
    private final WindowSize every;
    private final WindowSize terminate;
    private final QueryHandler<? super SelectionStatement, Object[]> handler;

    private final Lock lk = new ReentrantLock();
    private int status = READY;

    private final Buffer buffer;
    private final SamplerManager sampMgr;
    private int everyCount;
    private int terminateCount;
    private ScheduledFuture<?> everyThread;

    public SelectionExecutor(
            SelectionStatement query,
            Fpc fpc,
            QueryHandler<? super SelectionStatement, Object[]> handler) {
        this.fpc = fpc;
        this.query = query;
        selAtts = query.getAttributes();
        where = query.getWhere();
        every = query.getEvery();
        terminate = query.getTerminate();
        this.handler = handler;
        buffer = new ArrayBuffer(selAtts);
        sampMgr = new SamplerManager(
                query,
                fpc,
                new SamplerHandler()
        );
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
            if (terminate != null &&
                    terminate.getType() == WindowSize.WindowType.SAMPLE) {
                terminateCount = terminate.getSamples();
            }
            startEvery();
            sampMgr.start();
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
            sampMgr.stop();
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
     * Every runner
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
                handler.error(query, e);
                stop();
            } finally {
                lk.unlock();
            }
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
            List<Object[]> res = query.select(view);
            view.release();
            lk.lock();
            try {
                if (status != RUNNING) {
                    return;
                }
                checkTermination();
                res.forEach((r) -> handler.data(query, r));
            } finally {
                lk.unlock();
            }
        }

        private void checkTermination() {
            if (terminate != null &&
                    terminate.getType() != WindowSize.WindowType.SAMPLE) {
                return;
            }

            terminateCount--;
            if (terminateCount == 0) {
                stop();
            }
        }

    }


    /**
     * QueryHandler implementation for the sampler
     */
    private final class SamplerHandler implements
            QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable cause) {
            throw new RuntimeException("unimplemented");
        }

        @Override
        public void data(Sampling source, Object[] sample) {
            LogicValue valid = (LogicValue) where.run(sample, null);
            if (!valid.toBoolean()) {
                return;
            }

            lk.lock();
            try {
            	System.out.println("Sam"+sample[0]);
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
                handler.error(query, e);
                stop();
            }
        }


    }

}
