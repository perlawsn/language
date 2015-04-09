package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Record;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.IfEvery;
import org.dei.perla.lang.executor.statement.Refresh;
import org.dei.perla.lang.executor.statement.Refresh.RefreshType;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 24/03/15.
 */
public final class Sampler {

    private static final int INITIALIZING = 0;
    private static final int SETTING_RATE = 1;
    private static final int SAMPLING = 2;
    private static final int STOPPED = 3;

    private final Fpc fpc;
    private final SamplingIfEvery sampling;
    private final Refresh refresh;
    private final IfEvery ife;

    private final SamplingHandler sampHandler;
    private final TaskHandler ifeHandler;

    private final AtomicInteger status = new AtomicInteger(INITIALIZING);
    private volatile Duration rate = Duration.ofSeconds(0);

    private volatile Task ifeTask = null;
    private Task samplingTask;
    private Task eventTask;

    protected Sampler(SamplingIfEvery sampling, Fpc fpc)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");
        Conditions.checkIllegalArgument(sampling.hasErrors(),
                "Sampling clause contains errors.");

        this.fpc = fpc;
        this.sampling = sampling;
        this.refresh = sampling.getRefresh();
        this.ife = sampling.getIfEvery();

        sampHandler = new SamplingHandler();
        ifeHandler = new IfEveryHandler();

        ifeTask = fpc.get(sampling.getIfEveryAttributes(), ifeHandler);
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
            if (refresh == null  || ifeTask != null ||
                    refresh.getRefreshType() == RefreshType.EVENT) {
                return;
            }

            // TODO: check!
            ifeTask = fpc.get(sampling.getIfEveryAttributes(),
                    refresh.getDuration(), ifeHandler);
        }

        @Override
        public void newRecord(Task task, Record record) {
            if (status.compareAndSet(SAMPLING, SETTING_RATE)) {
                Duration d = ife.run(record.values());
                if (d == rate) {
                    status.set(SAMPLING);
                    return;
                }

                rate = d;
                samplingTask.stop();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: Escalate
        }

    }

    /**
     * @author Guido Rota 30/03/2015
     */
    private class SamplingHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (status.intValue() != SETTING_RATE) {
                return;
            }

            samplingTask = fpc.get(sampling.getIfEveryAttributes(), rate,
                    sampHandler);
            status.set(SAMPLING);
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
