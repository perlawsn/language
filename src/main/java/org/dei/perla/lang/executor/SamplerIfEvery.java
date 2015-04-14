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
import org.dei.perla.lang.executor.statement.Sampling;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 24/03/15.
 */
public final class SamplerIfEvery implements Sampler {

    // Exceptions
    private static final QueryException IFE_INIT_EXCEPTION =
            new QueryException("Initialization of IF EVERY sampling failed, " +
                    "cannot retrieve sample the required attributes");

    private static final QueryException IFE_SAMPLING_EXCEPTION =
            new QueryException("Sampling of IF EVERY sampling attributes " +
                    "failed");

    private static final QueryException EVT_INIT_EXCEPTION =
            new QueryException("Initialization of REFRESH ON EVENT sampling " +
                    "failed, cannot retrieve the required events");

    private static final QueryException EVT_SAMPLING_EXCEPTION =
            new QueryException("Sampling of REFRESH ON EVENT events failed");

    private static final QueryException EVT_STOPPED_EXCEPTION =
            new QueryException("REFRESH ON EVENT sampling stopped prematurely");

    private static final QueryException SAMP_STOPPED_EXCEPTION =
            new QueryException("Sampling operation stopped prematurely");

    private static final QueryException SAMP_EXCEPTION =
            new QueryException("Unexpected error while sampling");

    // Sampler status
    private static final int INITIALIZING = 0;
    private static final int NEW_RATE = 1;
    private static final int SAMPLING = 2;
    private static final int STOPPED = 3;

    private final SamplingIfEvery sampling;
    // Attributes required by the data management section of the query
    private final List<Attribute> atts;
    private final Fpc fpc;
    private final QueryHandler<Sampling, Object[]> handler;
    private final Refresh refresh;
    private final IfEvery ife;

    private final TaskHandler sampHandler = new SamplingHandler();
    private final TaskHandler ifeHandler = new IfEveryHandler();
    private final TaskHandler evtHandler = new EventHandler();

    private final Lock lk = new ReentrantLock();
    // Current status
    private volatile int status = STOPPED;
    // Current sampling rate
    private Duration rate = Duration.ofSeconds(0);

    // Running sampling tasks
    private Task ifeTask = null;
    private Task sampTask = null;
    private Task evtTask = null;

    protected SamplerIfEvery(SamplingIfEvery sampling, List<Attribute> atts, Fpc fpc,
            QueryHandler<Sampling, Object[]> handler)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");
        Conditions.checkIllegalArgument(!sampling.hasErrors(),
                "Sampling clause contains errors.");

        this.sampling = sampling;
        this.atts = atts;
        this.fpc = fpc;
        this.handler = handler;
        this.refresh = sampling.getRefresh();
        this.ife = sampling.getIfEvery();

    }

    @Override
    public boolean isRunning() {
        return status != STOPPED;
    }

    @Override
    public void start() throws QueryException {
        if (status != STOPPED) {
            throw new IllegalStateException(
                    "Cannot start, SamplerIfEvery is already running");
        }

        lk.lock();
        try {
            status = INITIALIZING;
            Task t = fpc.get(sampling.getIfEveryAttributes(), true, ifeHandler);
            if (t == null) {
                throw IFE_INIT_EXCEPTION;
            }
        } finally {
            lk.unlock();
        }
    }

    @Override
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
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * @param e Exception to propagate
     */
    private void handleError(Exception e) {
        stop();
        handler.error(sampling, e);
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
                if (refresh == null) {
                    return;

                } else if (refresh.getType() == RefreshType.EVENT &&
                        evtTask == null) {
                    evtTask = fpc.async(refresh.getEvents(), false, evtHandler);
                    if (evtTask == null) {
                        handleError(EVT_INIT_EXCEPTION);
                    }

                } else if (refresh.getType() == RefreshType.TIME){
                    ifeTask = fpc.get(sampling.getIfEveryAttributes(), true,
                            refresh.getDuration(), ifeHandler);
                    if (ifeTask == null) {
                        handleError(IFE_INIT_EXCEPTION);
                    }
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

            handleError(IFE_SAMPLING_EXCEPTION);
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
                    handleError(SAMP_STOPPED_EXCEPTION);
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

            handler.data(sampling, record.values());
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (status == STOPPED) {
                return;
            }

            handleError(SAMP_EXCEPTION);
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
                    handleError(EVT_STOPPED_EXCEPTION);
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
                Task t = fpc.get(sampling.getIfEveryAttributes(), true,
                        ifeHandler);
                if (t == null) {
                    handleError(IFE_INIT_EXCEPTION);
                }
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            handleError(EVT_SAMPLING_EXCEPTION);
        }

    }

}
