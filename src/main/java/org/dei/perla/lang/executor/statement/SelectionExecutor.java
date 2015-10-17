package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.*;
import org.dei.perla.core.utils.AsyncUtils;
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

    private final SelectionStatement query;
    private final Select select;
    private final ExecutionConditions execCond;
    private final Expression where;

    private final QueryHandler<? super SelectionStatement, Object[]> handler;
    private final Fpc fpc;

    private final Buffer buffer;

    private final SamplerRunner sampler;
    private final SamplerHandler sampHand = new SamplerHandler();
    private final Runnable selectRunnable = new SelectRunnable();

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

    public SelectionExecutor(SelectionStatement query,
            QueryHandler<? super SelectionStatement, Object[]> handler,
            Fpc fpc) {
        this.query = query;
        select = query.getSelect();
        execCond = query.getExecutionConditions();
        where = query.getWhere();
        this.fpc = fpc;
        this.handler = handler;

        // TODO: forecast average buffer length
        buffer = new ArrayBuffer(query.getAttributes(), 512);
        sampler = new SamplerRunner(query, fpc, sampHand);

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
