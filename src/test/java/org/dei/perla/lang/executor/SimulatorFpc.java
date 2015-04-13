package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.core.record.SamplePipeline;
import org.dei.perla.core.record.SamplePipeline.PipelineBuilder;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author Guido Rota 13/04/15.
 */
public class SimulatorFpc implements Fpc {

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();

    private final Map<Long, Integer> periods = new HashMap<>();
    private int periodicCount = 0;
    private int asyncCount = 0;

    private final Object[] values;
    private final List<Attribute> atts;

    public SimulatorFpc(Map<Attribute, Object> values) {
        this.values = new Object[values.size()];
        atts = new ArrayList<>();
        int i = 0;
        for (Map.Entry<Attribute, Object> e : values.entrySet()) {
            atts.add(e.getKey());
            this.values[i++] = e.getValue();
        }
    }

    private void addPeriod(long period) {
        Integer count = periods.get(period);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        periods.put(period, count);
    }

    private void removePeriod(long period) {
        Integer count = periods.get(period);
        if (count == null || count == 0) {
            throw new IllegalStateException(
                    "cannot remove non-existing period");
        }
        periods.put(period, count - 1);
    }

    public void setValues(Map<Attribute, Object> newValues) {
        lk.lock();
        try {
            if (values.length != newValues.size()) {
                throw new IllegalArgumentException("value array length mismatch");
            }
            newValues.forEach((a, v) -> {
                int i = atts.indexOf(a);
                values[i] = v;
            });
        } finally {
            lk.unlock();
        }
    }

    private Object[] newSample() {
        lk.lock();
        try {
            return Arrays.copyOf(values, values.length);
        } finally {
            lk.unlock();
        }
    }

    /**
     * Awaits until a sampling operation with the specified period is run on
     * this FPC.
     */
    public void awaitPeriod(long period) throws InterruptedException {
        lk.lock();
        try {
            while (!periods.containsKey(period)) {
                cond.await();
            }
        } finally {
            lk.unlock();
        }
    }

    /**
     * Returns true if the FPC is currently running a periodic sampling
     * operation whose period is equal to the value passed as parameter.
     */
    public boolean hasPeriod(long period) {
        lk.lock();
        try {
            return periods.containsKey(period);
        } finally {
            lk.unlock();
        }
    }

    /**
     * Returns the number of periodic sampling tasks being run in this FPC
     */
    public int countPeriodic() {
        lk.lock();
        try {
            return periodicCount;
        } finally {
            lk.unlock();
        }
    }

    /**
     * Returns the number of async sampling tasks being run in this FPC
     */
    public int countAsync() {
        lk.lock();
        try {
            return asyncCount;
        } finally {
            lk.unlock();
        }
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public String getType() {
        return "SimFpc";
    }

    @Override
    public Collection<Attribute> getAttributes() {
        return atts;
    }

    @Override
    public Task set(Map<Attribute, Object> values, boolean strict, TaskHandler handler) {
        throw new UnsupportedOperationException(
                "'set' operation not supported by SimFpc");
    }

    @Override
    public Task get(List<Attribute> atts, boolean strict, TaskHandler handler) {
        return new GetSimTask(atts, handler, this);
    }

    @Override
    public Task get(List<Attribute> atts, boolean strict, long periodMs, TaskHandler handler) {
        lk.lock();
        try {
            addPeriod(periodMs);
            periodicCount++;
            cond.signalAll();
            return new PeriodicSimTask(atts, periodMs, handler, this);
        } finally {
            lk.unlock();
        }
    }

    @Override
    public Task async(List<Attribute> atts, boolean strict, TaskHandler handler) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public void stop(Consumer<Fpc> handler) { }


    /**
     * @author guido Rota 13/04/15
     */
    private abstract class SimTask implements Task {

        protected final SamplePipeline pipeline;
        protected final SimulatorFpc fpc;
        protected final TaskHandler handler;

        protected SimTask(List<Attribute> atts, TaskHandler handler, SimulatorFpc fpc) {
            this.fpc = fpc;
            this.handler = handler;

            PipelineBuilder pb = SamplePipeline.newBuilder(atts);
            pb.reorder(atts);
            pipeline = pb.create();
        }

        @Override
        public List<Attribute> getAttributes() {
            return atts;
        }

    }


    /**
     * @author Guido Rota 13/04/15
     */
    public class GetSimTask extends SimTask {

        protected GetSimTask(List<Attribute> atts, TaskHandler handler,
                SimulatorFpc fpc) {
            super(atts, handler, fpc);
            Record r = pipeline.run(fpc.newSample());
            handler.newRecord(this, r);
            handler.complete(this);
        }

        @Override
        public boolean isRunning() {
            return false;
        }

        @Override
        public void stop() { }

    }

    /**
     * @author Guido Rota 13/04/15
     */
    public class PeriodicSimTask extends SimTask {

        private volatile boolean running = true;
        private final long periodMs;
        private final Thread generator;

        protected PeriodicSimTask(List<Attribute> atts, long periodMs,
                TaskHandler handler, SimulatorFpc fpc) {
            super(atts, handler, fpc);
            this.periodMs = periodMs;
            generator = new Thread(this::generateValues);
            generator.start();
        }

        private void generateValues() {
            try {
                while (true) {
                    Record r = pipeline.run(fpc.newSample());
                    handler.newRecord(this, r);
                    Thread.sleep(periodMs);
                }
            } catch(InterruptedException e) {
                running = false;
                handler.complete(this);
            }
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public void stop() {
            lk.lock();
            try {
                running = false;
                generator.interrupt();
                periodicCount--;
                removePeriod(periodMs);
            } finally {
                lk.unlock();
            }
        }

    }

}
