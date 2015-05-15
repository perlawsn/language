package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.core.utils.AsyncUtils;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.*;
import org.dei.perla.lang.query.statement.WindowSize.WindowType;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Guido Rota 22/04/15.
 */
public final class SelectionExecutor {

    private static final int ERROR = 0;
    private static final int STOPPED = 1;
    private static final int READY = 2;
    private static final int INITIALIZING = 3;
    private static final int RUNNING = 4;
    private static final int PAUSED = 5;

    private static final ExecutorService pool =
            Executors.newCachedThreadPool();
    private static final ScheduledExecutorService timer =
            Executors.newSingleThreadScheduledExecutor();

    private final SelectionQuery query;
    private final Select select;
    private final ExecutionConditions execCond;
    private final Expression where;

    private final QueryHandler<? super SelectionQuery, Object[]> handler;
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

    private volatile int status = READY;

    // Number of samples to receive before triggering a selection operation.
    // This value is used to reset the samplesLeft counter.
    private final int sampleCount;

    // Number of samples still to receive until the next selection operation is
    // triggered
    private final AtomicInteger samplesLeft = new AtomicInteger(0);

    // Number of times the data management clause has been triggered
    private final AtomicInteger triggered = new AtomicInteger(0);

    // Number of record to generate before terminating the query
    private final int recordsToTermination;

    // Total number of records produced by the query. It is only employed as
    // part of the mechanism used to terminate query execution in a
    // sample-based terminate after clause, so we don't really care if it
    // overflows.
    private volatile int recordsProduced = 0;

    public SelectionExecutor(SelectionQuery query,
            QueryHandler<? super SelectionQuery, Object[]> handler,
            Fpc fpc) {
        this.query = query;
        select = query.getSelect();
        execCond = query.getExecutionConditions();
        Conditions.checkIllegalArgument(execCond.isComplete(),
                "Incomplete execution condition");
        where = query.getWhere();
        this.fpc = fpc;
        this.handler = handler;

        // TODO: forecast average buffer length
        buffer = new ArrayBuffer(query.getDataAttributes(), 512);
        sampler = createSampler(query.getSampling(),
                query.getDataAttributes());

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
     * condition turns false during query execution. Under such conditions
     * both the {@code isRunning()} and {@code isPaused()} methods will
     * return true.
     *
     * @return true if the executor is running, false otherwise
     */
    public synchronized boolean isRunning() {
        return status >= INITIALIZING;
    }

    /**
     * Checks if the {@code SelectExecutor} has been paused. The only
     * intended use of this method is inside the query executor unit tests.
     *
     * @return true if the executor is paused, false otherwise
     */
    protected synchronized boolean isPaused() {
        return status == PAUSED;
    }

    /**
     * Starts the execution of the {@link SelectionQuery}. Startup errors
     * will be asynchronously notified through the {@link QueryHandler}
     * specified in the constructor after the {@code start()} method is
     * over.
     */
    public synchronized void start() {
        if (status != READY) {
            throw new IllegalStateException(
                    "Cannot start, SelectExecutor has already been run");
        }

        if (isRunning()) {
            return;
        }

        startTerminateAfter(query.getTerminate());

        ExecutionConditions ec = query.getExecutionConditions();
        if (ec.getAttributes().isEmpty()) {
            status = RUNNING;
            startEvery(query.getEvery());
            // Start sampling immediately if the execution condition is
            // empty or if its value is static.
            sampler.start();

        } else {
            // Evaluate the execution condition before starting the
            // main sampling operation
            status = INITIALIZING;
            List<Attribute> as = execCond.getAttributes();
            Task t = fpc.get(as, true, execIfTaskHand);
            if (t == null) {
                status = ERROR;
                notifyErrorAsync(executeIfStartErrorString(as));
            }
        }
    }

    private String executeIfStartErrorString(List<Attribute> atts) {
        StringBuilder bld =
                new StringBuilder("Error starting EXECUTE-IF sampling: ");
        bld.append("cannot retrieve attributes ");
        atts.forEach(e -> bld.append(e).append(" "));
        bld.append("from FPC ").append(fpc.getId());
        return bld.toString();
    }

    /*
     * Starts the TERMINATE AFTER clause
     */
    private void startTerminateAfter(WindowSize terminate) {
        if (terminate.isZero() ||
                terminate.getType() == WindowType.SAMPLE) {
            return;
        }

        long delayMs = terminate.getDuration().toMillis();
        terminateTimer = timer.schedule(this::stop, delayMs,
                TimeUnit.MILLISECONDS);
    }

    /*
     * starts the EVERY clause
     */
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

    /**
     * Stops the executor. After this method is called, the {@code
     * SelectExecutor} cannot be re-started again.
     */
    public synchronized void stop() {
        if (status == STOPPED) {
            return;
        }

        status = STOPPED;
        stopExecution();
    }

    /**
     * Stops the execution of the selection query
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
     */
    private void stopExecution() {
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
        if (executeIfRefresher != null) {
            executeIfRefresher.stop();
            executeIfRefresher = null;
        }
    }

    /**
     * Pauses the query execution. It is important to note that the timer for
     * the time-based TERMINATE AFTER clause continues to run even when the
     * query is paused.
     *
     * NOTE: this method is not thread-safe and requires explicit
     * synchronization.
     */
    private void pause() {
        if (status == STOPPED) {
            handleError("Cannot pause, executor is not running");
        }

        status = PAUSED;
        sampler.stop();
        if (everyTimer != null) {
            everyTimer.cancel(false);
            everyTimer = null;
        }
        if (selectFuture != null) {
            selectFuture.cancel(false);
            selectFuture = null;
        }
    }

    /**
     * Resumes execution.
     *
     * NOTE: this method is not thread-safe and requires explicit
     * synchronization.
     */
    private void resume() {
        if (status != PAUSED) {
            handleError("Cannot resume, SelectExecutor is not in paused state");
        }

        sampler.start();
        startEvery(query.getEvery());
        startTerminateAfter(query.getTerminate());

        status = RUNNING;
    }

    /**
     * Simple utility method employed to asynchronously propagate an error
     *
     * @param msg error message
     */
    private void notifyErrorAsync(String msg) {
        AsyncUtils.runOnNewThread(() -> {
            synchronized (SelectionExecutor.this) {
                Exception e = new QueryException(msg);
                handler.error(query, e);
            }
        });
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
     *
     * @param msg error message
     * @param cause cause exception
     */
    private void handleError(String msg, Throwable cause) {
        status = ERROR;
        stopExecution();
        handler.error(query, new QueryException(msg, cause));
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
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
            synchronized (SelectionExecutor.this) {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while sampling data", cause);
            }
        }

        @Override
        public void data(Sampling source, Object[] sample) {
            // Avoiding strict synchronization to reduce latency on the
            // critical data path.
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
                // Update trigger information and start data management
                // thread (only when the query specifies a sample-based every
                // clause (i.e., sampleCount != 0)
                updateSampleCount();
            }
        }

        /**
         * Updates the number of samples received and checks if the data
         * management section is to be triggered
         */
        private void updateSampleCount() {
            int o, n;
            do {
                o = samplesLeft.intValue();
                if (o == 1) {
                    // Reset the counter, since the number of samples
                    // required to trigger a data management execution has
                    // been reached.
                    n = sampleCount;

                } else {
                    n = o - 1;
                }
            } while(!samplesLeft.compareAndSet(o, n));

            if (o - 1 == 0) {
                if (triggered.incrementAndGet() == 1 && status == RUNNING) {
                    selectFuture = pool.submit(selectRunnable);
                }
            }
        }

        /**
         * Triggers the data management section
         */
        private void dataManagementTrigger() {
            int o, n;
            do {
                o = triggered.intValue();
                if (o == Integer.MAX_VALUE) {
                    // This branch avoids integer overflow in those
                    // (supposedly rare) cases where the sampling period is
                    // excessively faster than the average execution time of
                    // the data management clause
                    n = o;

                } else {
                    n = o + 1;
                }
            } while(!triggered.compareAndSet(o, n));

            // The data management thread is started only if such thread is
            // not already executing (i.e., if it's still executing since the
            // last time it has been triggered
            if (n == 1 && status == RUNNING) {
                selectFuture = pool.submit(selectRunnable);
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
            synchronized (SelectionExecutor.this) {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while refreshing the EXECUTE-IF " +
                        "condition", cause);
            }
        }

        @Override
        public void data(Refresh source, Void value) {
            synchronized (SelectionExecutor.this) {
                List<Attribute> as = execCond.getAttributes();
                Task t = fpc.get(as, true, execIfTaskHand);
                if (t == null) {
                    handleError(executeIfStartErrorString(as));
                }
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
            synchronized (SelectionExecutor.this) {
                if (status == INITIALIZING) {
                    startExecIfRefresh(query.getExecutionConditions());
                    status = RUNNING;
                }

                if (status < RUNNING) {
                    return;
                }

                Expression cond = execCond.getCondition();
                LogicValue v = (LogicValue) cond.run(sample.values(), null);

                if (v.toBoolean()) {
                    resume();
                } else {
                    pause();
                }
            }
        }

        /*
         * Starts the REFRESH associated with the EXECUTE IF clause
         */
        private void startExecIfRefresh(ExecutionConditions ec) {
            // Avoid starting the refresher for the EXECUTE IF clause when not
            // necessary, i.e. if the refresh clause is trivially set to never or
            // if the execute if condition is constant and does not require any
            // data from the device in order to be evaluated.
            if (ec.getRefresh() == Refresh.NEVER ||
                    ec.getAttributes().isEmpty()) {
                return;
            }

            executeIfRefresher = new Refresher(ec.getRefresh(),
                    execIfRefHand, fpc);
            executeIfRefresher.start();
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SelectionExecutor.this) {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while sampling the attributes required to " +
                        "evaluate the EXECUTE-IF condition", cause);
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

                synchronized (SelectionExecutor.this) {
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
                }
            }
        }

    }

}
