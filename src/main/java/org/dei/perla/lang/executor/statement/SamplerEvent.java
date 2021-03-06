package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.*;
import org.dei.perla.core.utils.AsyncUtils;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SamplingEvent;

import java.util.List;

/**
 * SAMPLING ON EVENT clause executor.
 *
 * <p>
 * This executor can be stopped and re-started at will.
 *
 * @author Guido Rota 14/04/15.
 */
public final class SamplerEvent implements Sampler {

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int ERROR = 2;

    private final SamplingEvent sampling;
    private final Fpc fpc;
    private final List<Attribute> attributes;
    private final QueryHandler<? super Sampling, Object[]> handler;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler evtHandler = new EventHandler();

    private volatile int status = STOPPED;

    private Task evtTask;

    protected SamplerEvent(SamplingEvent sampling, Fpc fpc,
            List<Attribute> attributes,
            QueryHandler<? super Sampling, Object[]> handler)
            throws IllegalArgumentException {
        this.sampling = sampling;
        this.fpc = fpc;
        this.attributes = attributes;
        this.handler = handler;
    }

    @Override
    public synchronized void start() {
        if (status != STOPPED) {
            return;
        }

        List<Attribute> es = sampling.getEvents();
        evtTask = fpc.async(es, false, evtHandler);
        if (evtTask == null) {
            status = ERROR;
            notifyErrorAsync(createStartupError(es));
            return;
        }

        status = RUNNING;
    }

    /**
     * Simple utility method employed to asynchronously propagate an error
     *
     * @param msg error message
     */
    private void notifyErrorAsync(String msg) {
        AsyncUtils.runInNewThread(() -> {
            synchronized (SamplerEvent.this) {
                Exception e = new QueryException(msg);
                handler.error(sampling, e);
            }
        });
    }

    private String createStartupError(List<Attribute> events) {
        StringBuilder bld =
                new StringBuilder("Error starting SAMPLING ON EVENT executor: ");
        bld.append("cannot retrieve events ");
        events.forEach(e -> bld.append(e).append(" "));
        bld.append("from FPC ").append(fpc.getId());
        return bld.toString();
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
     * Stop sampling the triggering events.
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
        stopEventTask();
        handler.error(sampling, new QueryException(msg, cause));
    }


    /**
     * Handler used to sample the data required by the query
     *
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
     * Handler used to receive event notifications that will trigger a refresh
     *
     * @author Guido Rota 14/04/15
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            synchronized (SamplerEvent.this) {
                if (status == RUNNING && task == evtTask) {
                    handleError("REFRESH ON EVENT sampling stopped " +
                            "prematurely in SAMPLING ON EVENT clause", null);
                }
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            synchronized (SamplerEvent.this) {
                if (status != RUNNING) {
                    return;
                }

                fpc.get(attributes, false, sampHandler);
            }
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
