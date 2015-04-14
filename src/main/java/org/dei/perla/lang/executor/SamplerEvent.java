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

    private final SamplingEvent sampling;
    private final Fpc fpc;
    private final List<Attribute> atts;
    private final QueryHandler<Sampling, Object[]> handler;

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
        if (evtTask != null) {
            throw new IllegalStateException(
                    "Cannot start, SamplerIfEvery is already running");
        }

    }

    @Override
    public void stop() {
        if (evtTask == null) {
            return;
        }

    }

    @Override
    public boolean isRunning() {
        return evtTask != null;
    }


    /**
     * @author Guido Rota 14/04/15
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {

        }

        @Override
        public void newRecord(Task task, Record record) {

        }

        @Override
        public void error(Task task, Throwable cause) {

        }

    }

}
