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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private final ReadWriteLock lk = new ReentrantReadWriteLock();

    // Current status
    private volatile int status = STOPPED;

    // Current sampling rate
    private Duration rate = Duration.ZERO;

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
        lk.readLock().lock();
        try {
            return status != STOPPED;
        } finally {
            lk.readLock().unlock();
        }
    }

    @Override
    public void start() throws QueryException {
        lk.writeLock().lock();
        try {
            if (isRunning()) {
                return;
            }

            status = INITIALIZING;
            Task t = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
            if (t == null) {
                throw new QueryException("Initialization of IF EVERY sampling" +
                        " failed, cannot retrieve sample the required attributes");
            }
        } finally {
            lk.writeLock().unlock();
        }
    }

    @Override
    public void stop() {
        lk.writeLock().lock();
        try {
            status = STOPPED;
            rate = Duration.ZERO;

            if (ifeTask != null) {
                Task t = ifeTask;
                ifeTask = null;
                t.stop();
            }
            if (sampTask != null) {
                Task t = sampTask;
                sampTask = null;
                t.stop();
            }
            if (evtTask != null) {
                Task t = evtTask;
                evtTask = null;
                t.stop();
            }
            if (refresher != null) {
                refresher.stop();
            }
        } finally {
            lk.writeLock().unlock();
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
            lk.writeLock().lock();
            try {
                if (!isRunning()) {
                    return;
                }

                if (refresher != null && !refresher.isRunning()) {
                    refresher.start();
                }
            } catch (QueryException e) {
                handleError("Error starting refresh execution in IF-EVERY " +
                        "clause", e);
            } finally {
                lk.writeLock().unlock();
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            lk.writeLock().lock();
            try {
                if (status == SAMPLING) {
                    Duration d = ife.run(sample.values());
                    if (d == rate) {
                        // Set the status back to sampling if the new sampling rate
                        // is the same as the old one
                        return;
                    }
                    rate = d;
                    status = NEW_RATE;
                    sampTask.stop();
                    // The new sampling rate will be set in the SamplingHandler
                } else if (status == INITIALIZING) {
                    Duration d = ife.run(sample.values());
                    rate = d;
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;
                }
            } finally {
                lk.writeLock().unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            lk.writeLock().lock();
            try {
                if (!isRunning()) {
                    return;
                }

                handleError("Sampling of IF EVERY sampling attributes failed",
                        cause);
            } finally {
                lk.writeLock().unlock();
            }
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
            lk.writeLock().lock();
            try {
                if (status == NEW_RATE) {
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;

                } else if (status == SAMPLING && task == sampTask) {
                    handleError("Sampling operation stopped prematurely");
                }
            } finally {
                lk.writeLock().unlock();
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            lk.readLock().lock();
            try {
                if (!isRunning()) {
                    return;
                }

                handler.data(sampling, sample.values());
            } finally {
                lk.readLock().unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            lk.writeLock().lock();
            try {
                if (!isRunning()) {
                    return;
                }

                handleError("Unexpected error while sampling", cause);
            } finally {
                lk.writeLock().unlock();
            }
        }

    }

    /**
     * Handler for the refresh clause executor
     *
     * @author Guido Rota 24/04/2015
     */
    private class RefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void error(Refresh source, Throwable cause) {
            lk.writeLock().lock();
            try {
                if (!isRunning()) {
                    return;
                }

                handleError("Refresh execution error in IF-EVERY clause", cause);
            } finally {
                lk.writeLock().unlock();
            }
        }

        @Override
        public void data(Refresh source, Void value) {
            lk.writeLock().lock();
            try {
                if (!isRunning()) {
                    return;
                }

                Task t = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
                if (t == null) {
                    handleError("Initialization of IF EVERY sampling" +
                            " failed, cannot retrieve sample the required attributes");
                }
            } finally {
                lk.writeLock().unlock();
            }
        }

    }

}
