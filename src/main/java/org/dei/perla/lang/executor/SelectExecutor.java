package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.sample.Attribute;
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
public class SelectExecutor {

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;
    private static final int TERMINATED = 3;

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

    private AtomicInteger status = new AtomicInteger(STOPPED);

    // Number of times the sampling operation has been triggered
    private volatile int triggered = 0;

    // Sample count lock
    private final Lock lk = new ReentrantLock();

    // Number of samples to receive before triggering a selection operation.
    // This value is used to reset the samplesLeft counter.
    private final int sampleCount;

    // Number of samples still to receive until the next selection operation is
    // triggered
    private int samplesLeft;

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
            samplesLeft = sampleCount;
        } else {
            sampleCount = 0;
            samplesLeft = 0;
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

    public void start() throws QueryException {
        if (!status.compareAndSet(STOPPED, RUNNING)) {
            return;
        }

        sampler.start();
        WindowSize every = query.getEvery();
        if (every.getType() == WindowType.TIME) {
            long periodMs = every.getDuration().toMillis();
            everyTimer = scheduleEveryTimer(periodMs);
        }
    }

    private ScheduledFuture<?> scheduleEveryTimer(long periodMs) {
        Runnable task = () -> {
            // TODO: schedule new selection
        };
        return timer.scheduleWithFixedDelay(task, periodMs, periodMs,
                TimeUnit.MILLISECONDS);
    }

    public void stop() {
        int old = status.getAndSet(STOPPED);
        if (old == STOPPED || old == TERMINATED) {
            return;
        }

        sampler.stop();
        if (everyTimer != null) {
            everyTimer.cancel(false);
        }
        if (selectFuture != null) {
            selectFuture.cancel(false);
        }
    }

    /**
     * Sampling handler class
     *
     * @author Guido Rota 22/04/2015
     */
    private class SamplerHandler implements QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable error) {
            // TODO: Stop and Escalate
        }

        @Override
        public void data(Sampling source, Object[] value) {
            buffer.add(value);
            if (sampleCount == 0) {
                // time-based every, nothing left to do
                return;
            }

            lk.lock();
            try {
                samplesLeft--;
                if (samplesLeft > 0) {
                    return;
                }

                samplesLeft += sampleCount;
                triggered++;
                if (triggered == 1) {
                    selectFuture = pool.submit(selectRunnable);
                }
            } finally {
                lk.unlock();
            }
        }

    }

    /**
     * @author Guido Rota 23/04/2015
     */
    private class SelectRunnable implements Runnable {

        @Override
        public void run() {
            do {
                // perform sampling
                BufferView view = buffer.unmodifiableView();
                List<Object[]> rs = select.select(view);
                rs.forEach(r -> handler.data(query, r));
                view.release();

                triggered--;
            } while (triggered > 0 && !Thread.interrupted());
        }

    }

}
