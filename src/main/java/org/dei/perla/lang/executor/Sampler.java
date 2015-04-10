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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 24/03/15.
 */
public final class Sampler {

    private static final String IFE_SAMPLING_ERROR = "Initialization of IF " +
            "EVERY sampling failed, FPC cannot sample the required attributes";

    private static final int INITIALIZING = 0;
    private static final int NEW_RATE = 1;
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

    private final Lock lk = new ReentrantLock();
    private volatile int status = INITIALIZING;
    private Duration rate = Duration.ofSeconds(0);

    private Task ifeTask = null;
    private Task sampTask = null;
    private Task evtTask = null;

    protected Sampler(SamplingIfEvery sampling, List<Attribute> atts, Fpc fpc)
            throws IllegalArgumentException, QueryExecutionException {
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
            throw new QueryExecutionException(IFE_SAMPLING_ERROR);
        }
    }

    public boolean isRunning() {
        return status != STOPPED;
    }

    public void stop() {
        if (status == STOPPED) {
            return;
        }

        lk.lock();
        try {
            status = STOPPED;
            if (ifeTask != null) {
                ifeTask.stop();
            }
            if (sampTask != null) {
                sampTask.stop();
            }
            if (evtTask != null) {
                evtTask.stop();
            }
        } finally {
            lk.unlock();
        }
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
            if (status == STOPPED) {
                return;
            }

            lk.lock();
            try {
                if (refresh == null ||
                        refresh.getRefreshType() == RefreshType.EVENT) {
                    return;
                }
                ifeTask = fpc.async(sampling.getIfEveryAttributes(), true,
                        ifeHandler);
                if (ifeTask == null) {
                    // TODO: escalate error
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void newRecord(Task task, Record record) {
            if (status == STOPPED) {
                return;
            }

            lk.lock();
            try {
                if (status == SAMPLING) {
                    Duration d = ife.run(record.values());
                    if (d == rate) {
                        // Set the status back to sampling if the new sampling rate
                        // is the same as the old one
                        return;
                    }
                    status = NEW_RATE;
                    rate = d;
                    sampTask.stop();
                    // The new sampling rate will be set in the SamplingHandler

                } else if (status == INITIALIZING) {
                    Duration d = ife.run(record.values());
                    rate = d;
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (status == STOPPED) {
                return;
            }

            // TODO: escalate error
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
            if (status == STOPPED) {
                return;
            }

            lk.lock();
            try {
                if (status == NEW_RATE) {
                    sampTask = fpc.get(atts, false, rate, sampHandler);
                    status = SAMPLING;
                } else if (status == SAMPLING) {
                    // TODO: Propagate error
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void newRecord(Task task, Record record) {
            if (status == STOPPED) {
                return;
            }

            // TODO: send the record to the local buffer
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (status == STOPPED) {
                return;
            }

            // TODO: Escalate error
        }

    }

    /**
     * TaskHandler employed to sample the REFRESH ON events
     *
     * @author Guido Rota 09/04/2015
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            lk.lock();
            try {
                if (status != STOPPED) {
                    // TODO: propagate error
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void newRecord(Task task, Record record) {
            lk.lock();
            try {
                // Single shot sampling when the refresh event is triggered
                ifeTask = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
                if (ifeTask == null) {
                    // TODO: Escalate error
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: escalate error
        }

    }

}
