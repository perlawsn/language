package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.LogicValue;
import org.dei.perla.lang.executor.statement.*;
import org.dei.perla.lang.executor.statement.WindowSize.WindowType;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 22/04/15.
 */
public final class SelectExecutor {

    private static final int STOPPED = 0;
    private static final int READY = 1;
    private static final int RUNNING = 2;
    private static final int PAUSED = 3;

    private static final ExecutorService pool =
            Executors.newCachedThreadPool();
    private static final ScheduledExecutorService timer =
            Executors.newSingleThreadScheduledExecutor();

    private final SelectionQuery query;
    private final Select select;
    private final ExecutionConditions execCond;
    private final Expression where;

    private final QueryHandler<SelectionQuery, Object[]> handler;
    private final Fpc fpc;

    private final Buffer buffer;

    private final Sampler sampler;
    private final SamplerHandler sampHand = new SamplerHandler();
    private final ExecIfRefreshHandler execIfRefHand = new ExecIfRefreshHandler();
    private final ExecIfTaskHandler execIfTaskHand = new ExecIfTaskHandler();
    private final Runnable selectRunnable = new SelectRunnable();

    private Refresher executeIfRefresher;
    private Future<?> selectFuture;
    private ScheduledFuture<?> everyTimer;
    private ScheduledFuture<?> terminateTimer;

    private final Lock lk = new ReentrantLock();
    private volatile int status = READY;

    // Number of samples to receive before triggering a selection operation.
    // This value is used to reset the samplesLeft counter.
    private final int sampleCount;

    // Number of samples still to receive until the next selection operation is
    // triggered
    private final AtomicInteger samplesLeft = new AtomicInteger(0);

    private final AtomicInteger triggered = new AtomicInteger(0);

    // Number of record to generate before terminating the query
    private final int recordsToTermination;

    // Total number of records produced by the query. It is only employed as
    // part of the mechanism used to terminate query execution in a
    // sample-based terminate after clause, so we don't really care if it
    // overflows.
    private volatile int recordsProduced = 0;

    public SelectExecutor(SelectionQuery query,
            QueryHandler<SelectionQuery, Object[]> handler, Fpc fpc) {
        this.query = query;
        select = query.getSelect();
        execCond = query.getExecutionConditions();
        where = query.getWhere();
        this.fpc = fpc;
        this.handler = handler;

        // TODO: forecast average buffer length
        buffer = new ArrayBuffer(query.getSelectAttributes(), 512);
        sampler = createSampler(query.getSampling(),
                query.getSelectAttributes());

        // Initialize EVERY data
        WindowSize every = query.getEvery();
        switch (every.getType()) {
            case SAMPLE:
                sampleCount = every.getSamples();
                samplesLeft.set(sampleCount);
                break;
            case TIME:
                sampleCount = 0;
                break;
            default:
                throw new RuntimeException("Unexpected WindowSize type " +
                        every.getType() + " found in EVERY clause");
        }

        // Initialize TERMINATE data
        WindowSize terminate = query.getTerminate();
        switch (terminate.getType()) {
            case SAMPLE:
                recordsToTermination = terminate.getSamples();
                break;
            case TIME:
                recordsToTermination = 0;
                break;
            default:
                throw new RuntimeException("Unexpected WindowSize type " +
                        terminate.getType() + " found in TERMINATE clause");
        }
    }

    private Sampler createSampler(Sampling samp, List<Attribute> atts)
            throws IllegalArgumentException {
        if (samp instanceof SamplingIfEvery) {
            SamplingIfEvery sife = (SamplingIfEvery) samp;
            return new SamplerIfEvery(sife, atts, fpc, sampHand);

        } else if (samp instanceof SamplingEvent) {
            SamplingEvent sev = (SamplingEvent) samp;
            return new SamplerEvent(sev, atts, fpc, sampHand);

        } else {
            throw new IllegalArgumentException("Cannot start sampling of type" +
                    samp.getClass().getSimpleName());
        }
    }

    /**
     * Indicates if the query is running or not.
     *
     * <p>It is important to note that the executor may be running even if no
     * new output records are produced. This may happen when the execution
     * condition turns false during query execution.
     *
     * @return true if the executor is running, false otherwise
     */
    public boolean isRunning() {
        lk.lock();
        try {
            return status >= RUNNING;
        } finally {
            lk.unlock();
        }
    }

    public void start() throws QueryException {
        lk.lock();
        try {
            if (status != READY) {
                throw new IllegalStateException(
                        "Cannot restart SelectExecutor");
            }

            sampler.start();
            startEvery(query.getEvery());
            startExecuteIf(query.getExecutionConditions());
            startTerminateAfter(query.getTerminate());

            status = RUNNING;
        } finally {
            lk.unlock();
        }
    }

    private void startEvery(WindowSize every) {
        if (every.getType() != WindowType.TIME) {
            return;
        }

        long periodMs = every.getDuration().toMillis();
        Runnable task = () -> {
            if (triggered.incrementAndGet() == 1) {
                selectFuture = pool.submit(selectRunnable);
            }
        };
        everyTimer = timer.scheduleWithFixedDelay(task, periodMs, periodMs,
                TimeUnit.MILLISECONDS);
    }

    private void startTerminateAfter(WindowSize terminate) {
        if (terminate.isZero() ||
                terminate.getType() == WindowType.SAMPLE) {
            return;
        }

        long delayMs = terminate.getDuration().toMillis();
        terminateTimer = timer.schedule(this::stop, delayMs,
                TimeUnit.MILLISECONDS);
    }

    private void startExecuteIf(ExecutionConditions ec)
            throws QueryException {
        if (ec.getRefresh() == Refresh.NEVER) {
            return;
        }

        executeIfRefresher = new Refresher(ec.getRefresh(),
                execIfRefHand, fpc);
        executeIfRefresher.start();
    }

    public void stop() {
        lk.lock();
        try {
            if (status == STOPPED) {
                return;
            }

            status = STOPPED;
            sampler.stop();
            if (everyTimer != null) {
                everyTimer.cancel(false);
                everyTimer = null;
            }
            if (selectFuture != null) {
                selectFuture.cancel(false);
                selectFuture = null;
            }
            if (terminateTimer != null) {
                terminateTimer.cancel(false);
                terminateTimer = null;
            }
        } finally {
            lk.unlock();
        }
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * @param msg error message
     * @param cause cause exception
     */
    private void handleError(String msg, Throwable cause) {
        stop();
        handler.error(query, new QueryException(msg, cause));
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * @param msg error message
     */
    private void handleError(String msg) {
        handleError(msg, null);
    }


    /**
     * Sampling handler class
     *
     * @author Guido Rota 22/04/2015
     */
    private final class SamplerHandler
            implements QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable cause) {
            lk.lock();
            try {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while sampling data", cause);
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void data(Sampling source, Object[] sample) {
            if (status != RUNNING) {
                return;
            }

            // Check if the new sample satisfies the WHERE condition
            LogicValue v = (LogicValue) where.run(sample, null);
            if (!v.toBoolean()) {
                return;
            }

            // Add sample to the buffer
            buffer.add(sample);

            if (sampleCount != 0) {
                // Update trigger information and start data management thread
                sampleTrigger();
            }
        }

        private void sampleTrigger() {
            int o, n;
            do {
                o = samplesLeft.intValue();
                if (o - 1 == 0) {
                    n = sampleCount;

                } else if (o == Integer.MIN_VALUE) {
                    // The speed of the sampling operation is excessively
                    // faster than the speed of the data management cycle.
                    // This if branch avoids integer underflow
                    n = o;

                } else {
                    n = o - 1;
                }
            } while(!samplesLeft.compareAndSet(o, n));

            if (o - 1 == 0) {
                if (triggered.incrementAndGet() == 1) {
                    selectFuture = pool.submit(selectRunnable);
                }
            }
        }

    }


    /**
     * Execution condition refresh handler
     *
     * @author Guido Rota 23/04/2015
     */
    private class ExecIfRefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void error(Refresh source, Throwable cause) {
            lk.lock();
            try {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while refreshing EXECUTE-IF condition",
                        cause);
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void data(Refresh source, Void value) {
            Task t = fpc.get(execCond.getAttributes(), true, execIfTaskHand);
            if (t == null) {
                handleError("Cannot start sampling task to retrieve data " +
                        "required to evaluate the EXECUTE-IF clause");
            }
        }

    }


    /**
     * Execution condition sampling handler
     *
     * @author Guido Rota 28/04/2015
     */
    private class ExecIfTaskHandler implements TaskHandler {

        @Override
        public void complete(Task task) { }

        @Override
        public void data(Task task, Sample sample) {
            Expression cond = execCond.getCondition();
            LogicValue v = (LogicValue) cond.run(sample.values(), null);

            if (v.toBoolean()) {
                // TODO: RESUME
                throw new RuntimeException("unimplemented");
            } else {
                // TODO: PAUSE
                throw new RuntimeException("unimplemented");
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            lk.lock();
            try {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while sampling data to compute" +
                        "EXECUTE-IF clause");
            } finally {
                lk.unlock();
            }
        }

    }


    /**
     * Data management thread code
     *
     * @author Guido Rota 23/04/2015
     */
    private class SelectRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted() && status == RUNNING) {
                // Execute data management section
                BufferView view = buffer.unmodifiableView();
                List<Object[]> rs = select.select(view);

                lk.lock();
                try {
                    // Notify new records and check termination conditions.
                    // the following operations are performed under lock to
                    // guarantee precise TERMINATE AFTER semantics
                    rs.forEach(r -> handler.data(query, r));
                    view.release();

                    // Check sample-based termination condition
                    recordsProduced++;
                    if (recordsToTermination != 0 &&
                            recordsProduced == recordsToTermination) {
                        stop();
                    }

                    // Check if a new selection was triggered while this was running
                    if (triggered.decrementAndGet() == 0) {
                        return;
                    }
                } finally {
                    lk.unlock();
                }
            }
        }

    }

}
