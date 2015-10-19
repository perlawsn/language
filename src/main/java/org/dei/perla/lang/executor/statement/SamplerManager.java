package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.*;
import org.dei.perla.core.utils.AsyncUtils;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.*;

import java.util.List;

/**
 * Execute-if clause executor class. It's responsible for starting and
 * stopping the sampling operation based on the current device's status.
 *
 * @author Guido Rota 01/10/15.
 */
public final class SamplerManager {

    private static final int ERROR = 0;
    private static final int STOPPED = 1;
    private static final int READY = 2;
    private static final int INITIALIZING = 3;
    private static final int RUNNING = 4;
    private static final int PAUSED = 5;

    private final Fpc fpc;

    private final Sampling sampling;
    private final ExecutionConditions cond;
    private final QueryHandler<? super Sampling, Object[]> handler;

    private Refresher executeIfRefresher;
    private final ExecIfRefreshHandler execIfRefHand = new ExecIfRefreshHandler();
    private final ExecIfTaskHandler execIfTaskHand = new ExecIfTaskHandler();

    private final Sampler sampler;

    private volatile int status = READY;

    public SamplerManager(SelectionStatement query, Fpc fpc,
            QueryHandler<? super Sampling, Object[]> handler) {
        this.sampling = query.getSampling();
        this.cond = query.getExecutionConditions();
        this.fpc = fpc;
        this.handler = handler;
        sampler = createSampler(sampling, query.getAttributes(), handler);
    }

    private Sampler createSampler(Sampling samp, List<Attribute> atts,
            QueryHandler<? super Sampling, Object[]> handler)
            throws IllegalArgumentException {
        if (samp instanceof SamplingIfEvery) {
            SamplingIfEvery sife = (SamplingIfEvery) samp;
            return new SamplerIfEvery(sife, fpc, atts, handler);

        } else if (samp instanceof SamplingEvent) {
            SamplingEvent sev = (SamplingEvent) samp;
            return new SamplerEvent(sev, fpc, atts, handler);

        } else {
            throw new IllegalArgumentException("Cannot start sampling of type" +
                    samp.getClass().getSimpleName());
        }
    }

    public synchronized boolean isRunning() {
        return status >= INITIALIZING;
    }

    /**
     * Starts the {@code SamplerRunner}
     */
    public synchronized void start() {
        if (isRunning()) {
            return;
        } else if (status != READY) {
            throw new IllegalStateException(
                    "Cannot start, SelectExecutor has already been started");
        }

        if (cond.getAttributes().isEmpty()) {
            status = RUNNING;
            // Start sampling immediately if the execution condition is
            // empty or if its value is static.
            sampler.start();

        } else {
            // Evaluate the execution condition before starting the
            // main sampling operation.
            status = INITIALIZING;
            List<Attribute> as = cond.getAttributes();
            Task t = fpc.get(as, true, execIfTaskHand);
            if (t == null) {
                status = ERROR;
                executeIfSamplingError(as);
            }
        }
    }

    /**
     * Notifies asynchronously the {@link QueryHandler} that the EXECUTE IF
     * clause could not be evaluated due to missing attributes in the FPC
     *
     * @param atts list of {@link Attribute} required to evaluate the {@link
     * ExecutionConditions} clause
     */
    private void executeIfSamplingError(List<Attribute> atts) {
        StringBuilder bld =
                new StringBuilder("Error starting EXECUTE-IF sampling: ");
        bld.append("cannot retrieve attributes ");
        atts.forEach(e -> bld.append(e).append(" "));
        bld.append("from FPC ").append(fpc.getId());

        AsyncUtils.runInNewThread(() -> {
            synchronized (SamplerManager.this) {
                Exception e = new QueryException(bld.toString());
                handler.error(sampling, e);
            }
        });
    }

    /**
     * Stops the {@code SamplerRunner}
     */
    public synchronized void stop() {
        if (status == STOPPED) {
            return;
        }

        status = STOPPED;
        sampler.stop();
        if (executeIfRefresher != null) {
            executeIfRefresher.stop();
            executeIfRefresher = null;
        }
    }

    /**
     * Notifies the {@link QueryHandler} that an error has occurred. This
     * method stops the execution of the entire {@code SamplerRunner} class.
     *
     * @param message error message
     * @param cause cause of the error
     */
    private void handleError(String msg, Throwable cause) {
        handleError(new QueryException(msg, cause));
    }

    /**
     * Notifies the {@link QueryHandler} that an error has occurred. This
     * method stops the execution of the entire {@code SamplerRunner} class.
     *
     * @param message error message
     */
    private void handleError(String msg) {
        handleError(new QueryException(msg));
    }

    private void handleError(QueryException e) {
        stop();
        handler.error(sampling, e);
    }


    /**
     * Execution condition refresh handler
     *
     * @author Guido Rota 23/04/2015
     */
    private class ExecIfRefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void error(Refresh source, Throwable cause) {
            synchronized (SamplerManager.this) {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while refreshing the EXECUTE-IF " +
                        "condition", cause);
            }
        }

        @Override
        public void data(Refresh source, Void value) {
            synchronized (SamplerManager.this) {
                List<Attribute> as = cond.getAttributes();
                Task t = fpc.get(as, true, execIfTaskHand);
                if (t == null) {
                    handleError("SamplerRunner bug: FPC should be able to " +
                            "provide the required attributes");
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
            synchronized (SamplerManager.this) {
                if (status == INITIALIZING) {
                    status = RUNNING;
                    startExecIfRefresh(cond);
                } else if (status < RUNNING) {
                    return;
                }

                // Evaluate the execution condition and start/stop the
                // sampler accordingly
                Expression e = cond.getCondition();
                LogicValue v = (LogicValue) e.run(sample.values(), null);
                if (v.toBoolean()) {
                    sampler.start();
                } else {
                    sampler.stop();
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

            executeIfRefresher =
                    new Refresher(ec.getRefresh(), execIfRefHand, fpc);
            executeIfRefresher.start();
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerManager.this) {
                if (status < RUNNING) {
                    return;
                }

                handleError("Error while sampling the attributes required to " +
                        "evaluate the EXECUTE-IF condition", cause);
            }
        }

    }

}
