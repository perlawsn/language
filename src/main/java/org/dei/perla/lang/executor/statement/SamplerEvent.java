package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SamplingEvent;

import java.util.List;

/**
 * @author Guido Rota 14/04/15.
 */
public final class SamplerEvent implements Sampler {

    private final SamplingEvent sampling;
    private final Fpc fpc;
    private final List<Attribute> atts;
    private final QueryHandler<? super Sampling, Object[]> handler;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler evtHandler = new EventHandler();

    private volatile boolean running = false;

    private Task evtTask;

    protected SamplerEvent(SamplingEvent sampling, List<Attribute> atts, Fpc fpc,
            QueryHandler<? super Sampling, Object[]> handler)
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
        evtTask = fpc.async(sampling.getEvents(), false, evtHandler);
        if (evtTask == null) {
            throw new RuntimeException("Initialization of REFRESH ON EVENT " +
                    "sampling failed, cannot retrieve the required events");
        }
        running = true;
    }

    @Override
    public synchronized void stop() {
        running = false;
        if (evtTask != null) {
            Task t = evtTask;
            evtTask = null;
            t.stop();
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return running;
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
     * @author Guido Rota 14/04/15
     */
    private class SamplingHandler implements TaskHandler {

        @Override
        public void complete(Task task) { }

        @Override
        public void data(Task task, Sample sample) {
            // Not locking on purpose. We accept a weaker synchronization
            // guarantee in exchange for lower data latency
            if (!running) {
                return;
            }

            handler.data(sampling, sample.values());
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerEvent.this) {
                if (!running) {
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
                if (running && task == evtTask) {
                    handleError("REFRESH ON EVENT sampling stopped prematurely");
                }
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            // Not locking on purpose. We accept a weaker synchronization
            // guarantee in exchange for lower data latency
            if (!running) {
                return;
            }

            fpc.get(atts, false, sampHandler);
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized (SamplerEvent.this) {
                if (!running) {
                    return;
                }

                handleError("Sampling of REFRESH ON EVENT events failed", cause);
            }
        }

    }

}
