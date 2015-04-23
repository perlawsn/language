package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.statement.*;
import org.dei.perla.lang.executor.statement.WindowSize.WindowType;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.Condition;
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

    private final Fpc fpc;
    private final SelectionQuery query;
    private final Select select;

    private final Buffer buffer;

    private final Sampler sampler;
    private final SamplerHandler sampHand = new SamplerHandler();
    private final Thread everyThread;

    private final Lock lk = new ReentrantLock();
    private volatile int status = STOPPED;

    private final Condition selectCond = lk.newCondition();
    // Number of samples to receive before triggering a selection operation.
    // This value is used to reset the samplesLeft counter.
    private final int sampleCount;
    // Number of samples still to receive until the next selection operation is
    // triggered
    private volatile int samplesLeft;

    private final Duration everyPeriod;

    public SelectExecutor(SelectionQuery query, Fpc fpc) {
        this.fpc = fpc;
        this.query = query;
        select = query.getSelect();

        // TODO: forecast average buffer length
        buffer = new ArrayBuffer(query.getSelectAttributes(), 512);
        sampler = createSampler(query.getSampling(),
                query.getSelectAttributes());

        everyThread = new Thread(new EveryRunnable());
        WindowSize every = query.getEvery();
        switch (every.getType()) {
            case SAMPLE:
                sampleCount = every.getSamples();
                samplesLeft = sampleCount;
                everyPeriod = Duration.ZERO;
                break;
            case TIME:
                sampleCount = 0;
                samplesLeft = 0;
                everyPeriod = every.getDuration();
                break;
            default:
                throw new RuntimeException("Unkown WindowSize type found in " +
                        "EVERY clause" + every.getType());
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
        lk.lock();
        try {
            sampler.start();
            everyThread.start();
            // TODO: Complete
            status = RUNNING;
        } finally {
            lk.unlock();
        }
    }

    public void stop() {
        throw new RuntimeException("unimplemented");
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
            lk.lock();
            try {
                buffer.add(value);
                if (samplesLeft > Integer.MIN_VALUE) {
                    samplesLeft--;
                }
                if (samplesLeft != 0) {
                    samplesLeft--;
                } else {
                    selectCond.signal();
                }
            } finally {
                lk.unlock();
            }
        }

    }

    /**
     * @author Guido Rota 23/04/2015
     */
    private class EveryRunnable implements Runnable {

        @Override
        public void run() {
            while (status == RUNNING) {
                try {
                    waitEvery();
                } catch (InterruptedException e) {
                    if (status == RUNNING) {
                        // TODO: escalate
                    }
                }

                // TODO: perform selection

                // Update samples left only after the selection is done
                samplesLeft += sampleCount;
            }
        }

        private void waitEvery() throws InterruptedException {
            if (everyPeriod != Duration.ZERO) {
                Thread.sleep(everyPeriod.toMillis());

            } else {
                while (samplesLeft != 0 && status == RUNNING) {
                    selectCond.await();
                }
            }
        }

    }

}
