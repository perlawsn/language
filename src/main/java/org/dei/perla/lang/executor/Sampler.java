package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.IfEvery;
import org.dei.perla.lang.executor.statement.Refresh;
import org.dei.perla.lang.executor.statement.Refresh.RefreshType;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Guido Rota 24/03/15.
 */
public final class Sampler {

    private static final int INITIALIZING = 0;
    private static final int SETTING_RATE = 1;
    private static final int SAMPLING = 2;
    private static final int STOPPED = 3;

    private final SamplingIfEvery sampling;
    private final List<Attribute> atts;
    private final Fpc fpc;
    private final Refresh refresh;
    private final IfEvery ife;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler ifeHandler = new IfEveryHandler();
    private final TaskHandler evtHandler = new EventHandler();

    private final AtomicInteger status = new AtomicInteger(INITIALIZING);
    private volatile Duration rate = Duration.ofSeconds(0);

    private volatile Task ifeTask = null;
    private Task sampTask;
    private Task evtTask;

    protected Sampler(SamplingIfEvery sampling, List<Attribute> atts, Fpc fpc)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");
        Conditions.checkIllegalArgument(sampling.hasErrors(),
                "Sampling clause contains errors.");

        this.sampling = sampling;
        this.atts = atts;
        this.fpc = fpc;
        this.refresh = sampling.getRefresh();
        this.ife = sampling.getIfEvery();

        ifeTask = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
        if (ifeTask == null) {
            // TODO: improve this
            throw new RuntimeException("cannot sample");
        }
    }

    /**
     * @author Guido Rota 30/03/2015
     */
    private class IfEveryHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (refresh == null || status.intValue() == STOPPED ||
                    refresh.getRefreshType() == RefreshType.EVENT) {
                return;
            }

            ifeTask = fpc.async(sampling.getIfEveryAttributes(), true, ifeHandler);
            if (ifeTask == null) {
                // TODO: escalate error
            }
        }

        @Override
        public void newRecord(Task task, Record record) {
            // TODO: more states are probably needed
            if (status.compareAndSet(SAMPLING, SETTING_RATE)) {
                Duration d = ife.run(record.values());
                if (d == rate) {
                    status.set(SAMPLING);
                    return;
                }

                rate = d;
                sampTask.stop();
                // The new sampling rate will be set in the SamplingHandler

            } else if (status.compareAndSet(INITIALIZING, SETTING_RATE)) {
                Duration d= ife.run(record.values());
                rate = d;
                sampTask = fpc.get(atts, false, rate, sampHandler);
                status.set(SAMPLING);
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: escalate error
        }

    }

    /**
     * @author Guido Rota 09/04/2015
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (status.intValue() != STOPPED) {
                // TODO: propagate error
            }
        }

        @Override
        public void newRecord(Task task, Record record) {
            // Single shot sampling when the refresh event is triggered
            ifeTask = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
            if (ifeTask == null) {
                // TODO: Escalate error
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: escalate error
        }

    }

    /**
     * @author Guido Rota 30/03/2015
     */
    private class SamplingHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (status.compareAndSet(SETTING_RATE, SAMPLING)) {
                sampTask = fpc.get(atts, false, rate, sampHandler);
                status.set(SAMPLING);
                return;
            }

            if (status.intValue() == SAMPLING) {
                // TODO: propagate an error?
                return;
            } else if (status.intValue() == STOPPED) {
                return;
            }
        }

        @Override
        public void newRecord(Task task, Record record) {
            // TODO: send the record to the local buffer
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: Escalate error
        }

    }

}
