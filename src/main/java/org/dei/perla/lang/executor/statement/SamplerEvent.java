package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.core.utils.AsyncUtils;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SamplingEvent;

import java.util.List;

/**
 * SAMPLING ON EVENT clause executor. This executor can be stopped and
 * re-started at will.
 *
 * @author Guido Rota 14/04/15.
 */
public final class SamplerEvent implements Sampler {

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int ERROR = 2;

    private final SamplingEvent sampling;
    private final Fpc fpc;
    private final List<Attribute> atts;
    private final ClauseHandler<? super Sampling, Object[]> handler;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler evtHandler = new EventHandler();

    private volatile int status = STOPPED;

    private Task evtTask;

    protected SamplerEvent(SamplingEvent sampling, List<Attribute> atts, Fpc fpc,
            ClauseHandler<? super Sampling, Object[]> handler)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");

        this.sampling = sampling;
        this.fpc = fpc;
        this.atts = atts;
        this.handler = handler;
    }

    @Override
    public synchronized void start() {
        if (status != STOPPED) {
            return;
        }

        evtTask = fpc.async(sampling.getEvents(), false, evtHandler);
        if (evtTask != null) {
            status = ERROR;
            notifyErrorAsync("Error starting event sampling in SAMPLE ON " +
                    "EVENT clause executor");
        }

        status = RUNNING;
    }

    @Override
    public synchronized void stop() {
        if (status != RUNNING) {
            return;
        }

        status = STOPPED;
        stopEventTask();
    }

    /**
     * Stops the execution of the sampling-triggering events.
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
     */
    private void stopEventTask() {
        if (evtTask != null) {
            Task t = evtTask;
            evtTask = null;
            t.stop();
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return status == RUNNING;
    }

    /**
     * Simple utility method employed to asynchronously propagate an error
     *
     * @param msg error message
     */
    private void notifyErrorAsync(String msg) {
        AsyncUtils.runOnNewThread(() -> {
            synchronized (SamplerEvent.this) {
                Exception e = new QueryException(msg);
                handler.error(sampling, e);
            }
        });
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
     * @author Guido Rota 14/04/15
     */
    private class SamplingHandler implements TaskHandler {

        @Override
        public void complete(Task task) { }

        @Override
        public void data(Task task, Sample sample) {
            // Not locking on purpose. We accept a weaker synchronization
            // guarantee in exchange for lower data latency
            if (status != RUNNING) {
                return;
            }

            handler.data(sampling, sample.values());
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerEvent.this) {
                if (status != RUNNING) {
                    return;
                }

                handleError("Unexpected error while sampling", cause);
            }
        }

    }


    /**
     * @author Guido Rota 14/04/15
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            synchronized (SamplerEvent.this) {
                if (status == RUNNING && task == evtTask) {
                    handleError("REFRESH ON EVENT sampling stopped " +
                            "prematurely", null);
                }
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            // Not locking on purpose. We accept a weaker synchronization
            // guarantee in exchange for lower data latency
            if (status != RUNNING) {
                return;
            }

            fpc.get(atts, false, sampHandler);
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerEvent.this) {
                if (status != RUNNING) {
                    return;
                }

                handleError("Sampling of REFRESH ON EVENT events failed", cause);
            }
        }

    }

}
