package org.dei.perla.lang.simfpc;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.simfpc.SimTask.GetSimTask;
import org.dei.perla.lang.simfpc.SimTask.PeriodicSimTask;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Guido Rota 13/04/15.
 */
public class SimFpc implements Fpc {

    private final List<FpcAction> actions = new ArrayList<>();
    private final Object[] values;

    protected static final List<Attribute> ATTRIBUTES;
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

    public synchronized void setValues(Object[] newValues) {
        if (values.length != newValues.length) {
            throw new IllegalArgumentException("value array length mismatch");
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = newValues[i];
        }
    }

    public synchronized List<FpcAction> getActions() {
        return new ArrayList<>(actions);
    }

    public synchronized void addAction(FpcAction action) {
        actions.add(action);
    }

    protected synchronized Object[] newSample() {
        return Arrays.copyOf(values, values.length);
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
        return new PeriodicSimTask(atts, periodMs, handler, this);
    }

    @Override
    public Task async(List<Attribute> atts, boolean strict, TaskHandler handler) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public void stop(Consumer<Fpc> handler) { }

}
