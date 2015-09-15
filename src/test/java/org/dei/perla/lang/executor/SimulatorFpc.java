package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Sample;
import org.dei.perla.core.fpc.SamplePipeline;
import org.dei.perla.core.fpc.SamplePipeline.PipelineBuilder;
import org.dei.perla.core.utils.AsyncUtils;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * An instrumented FPC that simulates a basic data gathering device
 *
 * @author Guido Rota 13/04/15.
 */
public class SimulatorFpc implements Fpc {

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();

    private final Map<Long, Integer> periods = new HashMap<>();
    private final Set<PeriodicSimTask> periodicTasks = new HashSet<>();
    private final Set<AsyncSimTask> asyncTasks = new HashSet<>();

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

    /**
     * Convenience method for adding a period to the period map
     *
     * IMPORTANT: Must be called under lock
     */
    private void addPeriod(long period) {
        Integer count = periods.get(period);
        if (count == null) {
            count = 1;
        } else {
            count++;
        }
        periods.put(period, count);
    }

    /**
     * Convenience method for removing a period from the period map.
     *
     * IMPORTANT: Must be called under lock
     */
    private void removePeriod(long period) {
        Integer count = periods.get(period);
        if (count == null || count == 0) {
            throw new IllegalStateException(
                    "cannot remove non-existing period");
        }
        periods.put(period, count - 1);
    }

    /**
     * Triggers the production of a new sample on all event tasks
     */
    public void triggerEvent() {
        lk.lock();
        try {
            asyncTasks.forEach(AsyncSimTask::trigger);
        } finally {
            lk.unlock();
        }
    }

    /**
     * Triggers the production of an error
     */
    public void triggerError() {
        lk.lock();
        try {
            periodicTasks.forEach(PeriodicSimTask::triggerError);
            asyncTasks.forEach(AsyncSimTask::triggerError);
        } finally {
            lk.unlock();
        }
    }

    /**
     * Sets the values used by the FPC to generate new samples. This method
     * can be used to inject specific output values for testing purposes.
     */
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

    /**
     * Creates a new sample
     */
    private Object[] newSample() {
        lk.lock();
        try {
            return Arrays.copyOf(values, values.length);
        } finally {
            lk.unlock();
        }
    }

    /**
     * Awaits until at least one long-running smapling operation has started.
     *
     * @throws InterruptedException
     */
    public void awaitStarted() throws InterruptedException {
        lk.lock();
        try {
            while (asyncTasks.isEmpty() && periodicTasks.isEmpty()) {
                cond.await();
            }
        } finally {
            lk.unlock();
        }
    }

    /**
     * Awaits until all long-running sampling operations have stopped.
     *
     * @throws InterruptedException
     */
    public void awaitStopped() throws InterruptedException {
        lk.lock();
        try {
            while (!asyncTasks.isEmpty() || !periodicTasks.isEmpty()) {
                cond.await();
            }
        } finally {
            lk.unlock();
        }
    }

    /**
     * Awaits until all periodic sampling operations have stopped.
     *
     * @throws InterruptedException
     */
    public void awaitPeriodicStopped() throws InterruptedException {
        lk.lock();
        try {
            while (!periodicTasks.isEmpty()) {
                cond.await();
            }
        } finally {
            lk.unlock();
        }
    }

    /**
     * Awaits until a sampling operation with the specified period is run on
     * this FPC.
     *
     * @throws InterruptedException
     */
    public void awaitPeriod(long period) throws InterruptedException {
        lk.lock();
        try {
            while (!periods.containsKey(period) ||
                    periods.get(period) == 0) {
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
            return periodicTasks.size();
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
            return asyncTasks.size();
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
        lk.lock();
        try {
            // This synchronized block, along with the synchronized sample
            // creation (see GetSimTask), ensures that the handler is invoked
            // only after the call to this method terminates
            return new GetSimTask(atts, handler);
        } finally {
            lk.unlock();
        }
    }

    @Override
    public Task get(List<Attribute> atts, boolean strict, long periodMs, TaskHandler handler) {
        lk.lock();
        try {
            PeriodicSimTask t = new PeriodicSimTask(atts, periodMs, handler);
            t.start();
            periodicTasks.add(t);
            addPeriod(periodMs);
            cond.signalAll();
            return t;
        } finally {
            lk.unlock();
        }
    }

    @Override
    public Task async(List<Attribute> atts, boolean strict, TaskHandler handler) {
        lk.lock();
        try {
            AsyncSimTask t = new AsyncSimTask(atts, handler);
            asyncTasks.add(t);
            cond.signalAll();
            return t;
        } finally {
            lk.unlock();
        }
    }

    @Override
    public void stop(Consumer<Fpc> handler) {
        lk.lock();
        try {
            periodicTasks.forEach(Task::stop);
            asyncTasks.forEach(Task::stop);
        } finally {
            lk.unlock();
        }
    }


    /**
     * @author guido Rota 13/04/15
     */
    private abstract class SimTask implements Task {

        protected final SamplePipeline pipeline;
        protected final TaskHandler handler;

        protected SimTask(List<Attribute> atts, TaskHandler handler) {
            this.handler = handler;

            PipelineBuilder pb = SamplePipeline.newBuilder(
                    SimulatorFpc.this.atts);
            pb.reorder(atts);
            pipeline = pb.create();
        }

        @Override
        public List<Attribute> getAttributes() {
            return atts;
        }

    }


    /**
     * Simulated one-off sampling operation
     *
     * @author Guido Rota 13/04/15
     */
    private class GetSimTask extends SimTask {

        protected GetSimTask(List<Attribute> atts, TaskHandler handler) {
            super(atts, handler);
            AsyncUtils.runInNewThread(this::createSample);
        }

        private void createSample() {
            Sample r = pipeline.run(newSample());
            handler.data(this, r);
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
     * Simulated periodic sampling operation
     *
     * @author Guido Rota 13/04/15
     */
    public class PeriodicSimTask extends SimTask {

        private volatile boolean running = true;
        private final long periodMs;
        private final Thread generator;

        protected PeriodicSimTask(List<Attribute> atts, long periodMs,
                TaskHandler handler) {
            super(atts, handler);
            this.periodMs = periodMs;
            generator = new Thread(this::generateValues);
        }

        protected void start() {
            generator.start();
        }

        private void generateValues() {
            try {
                while (true) {
                    Sample r = pipeline.run(newSample());
                    handler.data(this, r);
                    Thread.sleep(periodMs);
                }
            } catch(InterruptedException e) {
                running = false;
                handler.complete(this);
            }
        }

        protected void triggerError() {
            Exception e = new RuntimeException("simulated error");
            handler.error(this, e);
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
                periodicTasks.remove(this);
                removePeriod(periodMs);
                cond.signalAll();
            } finally {
                lk.unlock();
            }
        }

    }


    /**
     * Simulated event sampling operation
     *
     * @author Guido Rota 14/04/15
     */
    private class AsyncSimTask extends SimTask {

        private volatile boolean running = true;

        private AsyncSimTask(List<Attribute> atts, TaskHandler handler) {
            super(atts, handler);
        }

        protected void trigger() {
            Sample r = pipeline.run(newSample());
            handler.data(this, r);
        }

        protected void triggerError() {
            Exception e = new RuntimeException("simulated error");
            handler.error(this, e);
        }

        @Override
        public boolean isRunning() {
            return running;
        }

        @Override
        public void stop() {
            lk.lock();
            try {
                asyncTasks.remove(this);
                cond.signalAll();
                running = false;
            } finally {
                lk.unlock();
            }
        }

    }

}
