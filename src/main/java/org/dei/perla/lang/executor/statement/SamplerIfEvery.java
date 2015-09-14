package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.*;
import org.dei.perla.core.utils.AsyncUtils;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.statement.IfEvery;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SamplingIfEvery;

import java.time.Duration;
import java.util.List;

/**
 * SAMPLING IF EVERY clause executor.
 *
 * <p>
 * This executor can be stopped and re-started at will.
 *
 * @author Guido Rota 24/03/15.
 */
public final class SamplerIfEvery implements Sampler {

    // Sampler status
    private static final int INITIALIZING = 0;
    private static final int NEW_RATE = 1;
    private static final int SAMPLING = 2;
    private static final int STOPPED = 3;
    private static final int ERROR = 4;

    private final SamplingIfEvery sampling;
    // Attributes required by the data management section of the query
    private final List<Attribute> atts;
    private final Fpc fpc;
    private final QueryHandler<? super Sampling, Object[]> handler;
    private final IfEvery ife;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler ifeHandler = new IfEveryHandler();
    private final QueryHandler<Refresh, Void> refHandler = new RefreshHandler();

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
            QueryHandler<? super Sampling, Object[]> handler)
            throws IllegalArgumentException {

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
    public synchronized boolean isRunning() {
        return status < STOPPED;
    }

    @Override
    public synchronized void start() {
        if (isRunning()) {
            return;
        }

        status = INITIALIZING;
        List<Attribute> as = sampling.getIfEveryAttributes();
        Task t = fpc.get(as, true, ifeHandler);
        if (t == null) {
            status = ERROR;
            notifyErrorAsync(samplingErrorString(as));
        }
    }

    /**
     * Simple utility method employed to asynchronously propagate an error
     *
     * @param msg error message
     */
    private void notifyErrorAsync(String msg) {
        AsyncUtils.runInNewThread(() -> {
            synchronized (SamplerIfEvery.this) {
                Exception e = new QueryException(msg);
                handler.error(sampling, e);
            }
        });
    }

    private String samplingErrorString(List<Attribute> atts) {
        StringBuilder bld =
                new StringBuilder("Error starting SAMPLING IF EVERY executor: ");
        bld.append("cannot retrieve attributes ");
        atts.forEach(e -> bld.append(e).append(" "));
        bld.append("from FPC ").append(fpc.getId());
        return bld.toString();
    }

    @Override
    public synchronized void stop() {
        status = STOPPED;
        stopExecution();
    }

    /**
     * Stops the execution of the sampling tasks.
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
     */
    public void stopExecution() {
        rate = Duration.ZERO;

        if (ifeTask != null) {
            ifeTask.stop();
            ifeTask = null;
        }
        if (sampTask != null) {
            sampTask.stop();
            sampTask = null;
        }
        if (evtTask != null) {
            evtTask.stop();
            evtTask = null;
        }
        if (refresher != null) {
            refresher.stop();
        }
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
        handler.error(sampling, new QueryException(msg, cause));
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
            synchronized (SamplerIfEvery.this) {
                if (!isRunning()) {
                    return;
                }

                // Stop here if there's no refresh clause to start or if the
                // refresh clause executor is already running
                if (refresher == null || refresher.isRunning()) {
                    return;
                }

                refresher.start();
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            synchronized (SamplerIfEvery.this) {
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
                    // The new sampling rate will be set in the
                    // SamplingHandler.complete method

                } else if (status == INITIALIZING) {
                    rate = ife.run(sample.values());
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;
                }
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerIfEvery.this) {
                if (!isRunning()) {
                    return;
                }

                handleError("Sampling of IF EVERY sampling attributes failed",
                        cause);
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
            synchronized (SamplerIfEvery.this) {
                if (status == NEW_RATE) {
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;

                } else if (status == SAMPLING && task == sampTask) {
                    handleError("Sampling operation stopped prematurely in " +
                            "SAMPLING IF EVERY executor", null);
                }
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            // Not locking on purpose. We accept a weaker synchronization
            // guarantee in exchange for lower data latency.
            if (status >= STOPPED) {
                return;
            }

            handler.data(sampling, sample.values());
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerIfEvery.this) {
                if (!isRunning()) {
                    return;
                }

                handleError("Unexpected sampling error in SAMPLER IF EVERY " +
                        "clause executor", cause);
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
            synchronized (SamplerIfEvery.this) {
                if (!isRunning()) {
                    return;
                }

                handleError("Refresh execution error in IF-EVERY clause", cause);
            }
        }

        @Override
        public void data(Refresh source, Void value) {
            synchronized (SamplerIfEvery.this) {
                if (!isRunning()) {
                    return;
                }

                List<Attribute> as = sampling.getIfEveryAttributes();
                Task t = fpc.get(as, true, ifeHandler);
                if (t == null) {
                    handleError(samplingErrorString(as), null);
                }
            }
        }

    }

}
