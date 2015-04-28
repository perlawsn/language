package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
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

    // TODO: add a TERMINATED condition to avoid re-starting of an executor
    // whose terminate-after has already expired
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;
    private static final int STOPPED = 4;

    private static final ExecutorService pool =
            Executors.newCachedThreadPool();
    private static final ScheduledExecutorService timer =
            Executors.newSingleThreadScheduledExecutor();

    private final SelectionQuery query;
    private final Select select;

    private final QueryHandler<SelectionQuery, Object[]> handler;
    private final Fpc fpc;

    private final Buffer buffer;

    private final Sampler sampler;
    private final SamplerHandler sampHand = new SamplerHandler();
    private final Runnable selectRunnable = new SelectRunnable();

    private Future<?> selectFuture;
    private ScheduledFuture<?> everyTimer;
    private ScheduledFuture<?> terminateTimer;

    private final Lock lk = new ReentrantLock();
    private volatile int status = STOPPED;

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
        WindowSize terminate = query.getEvery();
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
            return status < STOPPED;
        } finally {
            lk.unlock();
        }
    }

    public void start() throws QueryException {
        lk.lock();
        try {
            sampler.start();
            startEvery(query.getEvery());

            // EXECUTION CONDITIONS
            ExecutionConditions ec = query.getExecutionConditions();
            Refresh ecr = ec.getRefresh();
            switch (ecr.getType()) {
                case TIME:
                    // TODO: implement
                    throw new RuntimeException("unimplemented");
                case EVENT:
                    // TODO: implement
                    throw new RuntimeException("unimplemented");
            }

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

    public void stop() {
        lk.lock();
        try {
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
     * Sampling handler class
     *
     * @author Guido Rota 22/04/2015
     */
    private final class SamplerHandler
            implements QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable error) {
            // TODO: Stop and Escalate
        }

        @Override
        public void data(Sampling source, Object[] sample) {
            if (status != RUNNING) {
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


    private class RefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void error(Refresh source, Throwable cause) {

        }

        @Override
        public void data(Refresh source, Void value) {

        }

    }


    /**
     * @author Guido Rota 23/04/2015
     */
    private class SelectRunnable implements Runnable {

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                // Execute data management section and notify new record
                BufferView view = buffer.unmodifiableView();
                List<Object[]> rs = select.select(view);
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
