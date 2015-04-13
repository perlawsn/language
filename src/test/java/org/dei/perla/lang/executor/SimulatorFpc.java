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
    private final Set<Long> periods = new HashSet<>();

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

    public void awaitPeriod(long period) throws InterruptedException {
        lk.lock();
        try {
            while (!periods.contains(period)) {
                cond.await();
            }
        } finally {
            lk.unlock();
        }
    }

    public boolean hasPeriod(long period) {
        return periods.contains(period);
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
            periods.add(periodMs);
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
                periods.remove(periodMs);
            } finally {
                lk.unlock();
            }
        }

    }

}
