package org.dei.perla.lang.simfpc;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.simfpc.SimTask.GetSimTask;
import org.dei.perla.lang.simfpc.SimTask.PeriodicSimTask;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * @author Guido Rota 13/04/15.
 */
public class SimFpc implements Fpc {

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();
    private final Set<Long> periods = new HashSet<>();

    private final List<FpcAction> actions = new ArrayList<>();
    private final Object[] values;

    public static final List<Attribute> ATTRIBUTES;
    static {
        ATTRIBUTES = Arrays.asList(new Attribute[]{
                Attribute.create("temperature", DataType.INTEGER),
                Attribute.create("power", DataType.INTEGER)
        });
    }

    public SimFpc() {
        values = new Object[ATTRIBUTES.size()];
        values[0] = 23;
        values[1] = 100;
    }

    public void setValues(Object[] newValues) {
        lk.lock();
        try {
            if (values.length != newValues.length) {
                throw new IllegalArgumentException("value array length mismatch");
            }
            for (int i = 0; i < values.length; i++) {
                values[i] = newValues[i];
            }
        } finally {
            lk.unlock();
        }
    }

    public List<FpcAction> getActions() {
        lk.lock();
        try {
            return new ArrayList<>(actions);
        } finally {
            lk.unlock();
        }
    }

    public void addAction(FpcAction action) {
        lk.lock();
        try {
            actions.add(action);
        } finally {
            lk.unlock();
        }
    }

    protected Object[] newSample() {
        lk.lock();
        try {
            return Arrays.copyOf(values, values.length);
        } finally {
            lk.unlock();
        }
    }

    protected void awaitPeriod(long period) throws InterruptedException {
        lk.lock();
        try {
            while (!periods.contains(period)) {
                cond.await();
            }
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
        return ATTRIBUTES;
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

}
