package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 14/04/15.
 */
public class SamplerEvent implements Sampler {

    private static final int STOPPED = 0;
    private static final int STARTED = 1;

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
        Conditions.checkIllegalArgument(!sampling.hasErrors(),
                "Sampling clause contains errors.");

        this.sampling = sampling;
        this.fpc = fpc;
        this.atts = atts;
        this.handler = handler;
    }

    @Override
    public void start() throws QueryException {
        if (!status.compareAndSet(STOPPED, STARTED)) {
            throw new IllegalStateException(
                    "Cannot start, SamplerIfEvery is already running");
        }

        evtTask = fpc.async(sampling.getEvents(), false, evtHandler);
        if (evtTask == null) {
            status.set(STOPPED);
            throw new QueryException();
        }
    }

    @Override
    public void stop() {
        if (!status.compareAndSet(STARTED, STOPPED)) {
            return;
        }

        if (evtTask == null) {
            return;
        }
        evtTask.stop();
    }

    @Override
    public boolean isRunning() {
        return status.intValue() != STOPPED;
    }


    /**
     * @author Guido Rota 14/04/15
     */
    private class SamplingHandler implements TaskHandler {

        @Override
        public void complete(Task task) { }

        @Override
        public void newRecord(Task task, Record record) {
            if (status.intValue() == STOPPED) {
                return;
            }

            handler.data(sampling, record.values());
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (status.intValue() == STOPPED) {
                return;
            }

            handler.error(sampling, new QueryException(cause));
        }

    }


    /**
     * @author Guido Rota 14/04/15
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (status.intValue() == STOPPED) {
                return;
            }

            handler.error(sampling, new QueryException());
        }

        @Override
        public void newRecord(Task task, Record record) {
            if (status.intValue() == STOPPED) {
                return;
            }

            Task t = fpc.get(atts, false, sampHandler);
            if (t == null) {
                // TODO: notify error
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (status.intValue() == STOPPED) {
                return;
            }

            handler.error(sampling, new QueryException());
        }

    }

}
