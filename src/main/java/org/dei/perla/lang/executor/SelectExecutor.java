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

    private final Lock lk = new ReentrantLock();
    private volatile int status = STOPPED;

    // Number of samples to receive before triggering a selection operation.
    // This value is used to reset the samplesLeft counter.
    private final int sampleCount;

    // Number of samples still to receive until the next selection operation is
    // triggered
    private final AtomicInteger samplesLeft = new AtomicInteger(0);

    private final AtomicInteger triggered = new AtomicInteger(0);

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

        WindowSize every = query.getEvery();
        if (every.getType() == WindowType.SAMPLE) {
            sampleCount = every.getSamples();
            samplesLeft.set(sampleCount);
        } else {
            sampleCount = 0;
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
            WindowSize every = query.getEvery();
            if (every.getType() == WindowType.TIME) {
                long periodMs = every.getDuration().toMillis();
                everyTimer = scheduleEveryTimer(periodMs);
            }

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

            status = RUNNING;
        } finally {
            lk.unlock();
        }
    }

    private ScheduledFuture<?> scheduleEveryTimer(long periodMs) {
        Runnable task = () -> {
            if (triggered.incrementAndGet() == 1) {
                selectFuture = pool.submit(selectRunnable);
            }
        };
        return timer.scheduleWithFixedDelay(task, periodMs, periodMs,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        lk.lock();
        try {
            status = STOPPED;
            sampler.stop();
            if (everyTimer != null) {
                everyTimer.cancel(false);
            }
            if (selectFuture != null) {
                selectFuture.cancel(false);
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
                // perform sampling
                BufferView view = buffer.unmodifiableView();
                List<Object[]> rs = select.select(view);
                rs.forEach(r -> handler.data(query, r));
                view.release();

                if (triggered.decrementAndGet() == 0) {
                    return;
                }
            }
        }

    }

}
