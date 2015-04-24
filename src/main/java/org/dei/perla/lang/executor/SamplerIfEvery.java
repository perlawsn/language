package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.IfEvery;
import org.dei.perla.lang.executor.statement.Refresh;
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 24/03/15.
 */
public final class SamplerIfEvery implements Sampler {

    // Sampler status
    private static final int INITIALIZING = 0;
    private static final int NEW_RATE = 1;
    private static final int SAMPLING = 2;
    private static final int STOPPED = 3;

    private final SamplingIfEvery sampling;
    // Attributes required by the data management section of the query
    private final List<Attribute> atts;
    private final Fpc fpc;
    private final QueryHandler<Sampling, Object[]> handler;
    private final IfEvery ife;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler ifeHandler = new IfEveryHandler();
    private final QueryHandler<Refresh, Void> refHandler = new RefreshHandler();

    private final Lock lk = new ReentrantLock();
    // Current status
    private volatile int status = STOPPED;
    // Current sampling rate
    private Duration rate = Duration.ofSeconds(0);

    // Refresh clause executor
    private final Refresher refresher;

    // Running sampling tasks
    private Task ifeTask = null;
    private Task sampTask = null;
    private Task evtTask = null;

    protected SamplerIfEvery(SamplingIfEvery sampling, List<Attribute> atts, Fpc fpc,
            QueryHandler<Sampling, Object[]> handler)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");

        this.sampling = sampling;
        this.atts = atts;
        this.fpc = fpc;
        this.handler = handler;
        this.ife = sampling.getIfEvery();

        Refresh r = sampling.getRefresh();
        if (r != Refresh.NEVER) {
            refresher = new Refresher(r, refHandler, fpc);
        } else {
            refresher = null;
        }
    }

    @Override
    public boolean isRunning() {
        return status != STOPPED;
    }

    @Override
    public void start() throws QueryException {
        if (status != STOPPED) {
            throw new IllegalStateException(
                    "Cannot start, SamplerIfEvery is already running");
        }

        lk.lock();
        try {
            status = INITIALIZING;
            Task t = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
            if (t == null) {
                throw new QueryException("Initialization of IF EVERY sampling" +
                        " failed, cannot retrieve sample the required attributes");
            }
        } finally {
            lk.unlock();
        }
    }

    @Override
    public void stop() {
        lk.lock();
        if (status == STOPPED) {
            return;
        }

        try {
            status = STOPPED;
            if (ifeTask != null) {
                ifeTask.stop();
            }
            if (sampTask != null) {
                sampTask.stop();
            }
            if (evtTask != null) {
                evtTask.stop();
            }
            if (refresher != null) {
                refresher.stop();
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
        handler.error(sampling, new QueryException(msg, cause));
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
     * TaskHandler employed to sample the attributes required by the IF EVERY
     * clause
     *
     * @author Guido Rota 30/03/2015
     */
    private class IfEveryHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            lk.lock();
            try {
                if (status == STOPPED) {
                    return;
                }

                if (refresher != null && !refresher.isRunning()) {
                    refresher.start();
                }

            } catch (QueryException e) {
                handleError("Error starting refresh execution in IF-EVERY " +
                                "clause", e);
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            lk.lock();
            try {
                if (status == STOPPED) {
                    return;
                }

                if (status == SAMPLING) {
                    Duration d = ife.run(sample.values());
                    if (d == rate) {
                        // Set the status back to sampling if the new sampling rate
                        // is the same as the old one
                        return;
                    }
                    status = NEW_RATE;
                    rate = d;
                    sampTask.stop();
                    // The new sampling rate will be set in the SamplingHandler

                } else if (status == INITIALIZING) {
                    Duration d = ife.run(sample.values());
                    rate = d;
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (status == STOPPED) {
                return;
            }

            handleError("Sampling of IF EVERY sampling attributes failed",
                    cause);
        }

    }

    /**
     * TaskHandler employed to sample the SELECT attributes
     *
     * @author Guido Rota 30/03/2015
     */
    private class SamplingHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            lk.lock();
            try {
                if (status == STOPPED) {
                    return;
                }

                if (status == NEW_RATE) {
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;

                } else if (status == SAMPLING) {
                    handleError("Sampling operation stopped prematurely");
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            lk.lock();
            try {
                if (status == STOPPED) {
                    return;
                }

                handler.data(sampling, sample.values());
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            lk.lock();
            try {
                if (status == STOPPED) {
                    return;
                }

                handleError("Unexpected error while sampling", cause);
            } finally {
                lk.unlock();
            }
        }

    }

    private class RefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void error(Refresh source, Throwable cause) {
            handleError("Refresh execution error in IF-EVERY clause", cause);
        }

        @Override
        public void data(Refresh source, Void value) {
            Task t = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
            if (t == null) {
                handleError("Initialization of IF EVERY sampling" +
                        " failed, cannot retrieve sample the required attributes");
            }
        }

    }

}
