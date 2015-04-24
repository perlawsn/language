package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Guido Rota 14/04/15.
 */
public final class SamplerEvent implements Sampler {

    private static final int INITIALIZING = 0;
    private static final int RUNNING = 1;
    private static final int STOPPING = 2;
    private static final int STOPPED = 3;

    private final SamplingEvent sampling;
    private final Fpc fpc;
    private final List<Attribute> atts;
    private final QueryHandler<Sampling, Object[]> handler;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler evtHandler = new EventHandler();

    private final AtomicInteger status = new AtomicInteger(STOPPED);
    private volatile Task evtTask;

    protected SamplerEvent(SamplingEvent sampling, List<Attribute> atts, Fpc fpc,
            QueryHandler<Sampling, Object[]> handler)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");

        this.sampling = sampling;
        this.fpc = fpc;
        this.atts = atts;
        this.handler = handler;
    }

    @Override
    public void start() throws QueryException {
        while (!status.compareAndSet(STOPPED, INITIALIZING)) {
            if (isRunning()) {
                return;
            }
        }

        evtTask = fpc.async(sampling.getEvents(), false, evtHandler);
        if (evtTask == null) {
            status.set(STOPPED);
            throw new QueryException("Initialization of REFRESH ON EVENT " +
                    "sampling failed, cannot retrieve the required events");
        }

        status.set(RUNNING);
    }

    @Override
    public void stop() {
        while (!status.compareAndSet(RUNNING, STOPPING)) {
            if (!isRunning()) {
                return;
            }
        }

        if (evtTask != null) {
            Task t = evtTask;
            evtTask = null;
            t.stop();
        }

        status.set(STOPPED);
    }

    @Override
    public boolean isRunning() {
        return status.intValue() <= RUNNING;
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
            if (!isRunning()) {
                return;
            }

            handler.data(sampling, sample.values());
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (!isRunning()) {
                return;
            }

            handleError("Unexpected error while sampling", cause);
        }

    }


    /**
     * @author Guido Rota 14/04/15
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (isRunning() && task == evtTask) {
                handleError("REFRESH ON EVENT sampling stopped prematurely");
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            if (!isRunning()) {
                return;
            }

            fpc.get(atts, false, sampHandler);
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (!isRunning()) {
                return;
            }

            handleError("Sampling of REFRESH ON EVENT events failed", cause);
        }

    }

}
