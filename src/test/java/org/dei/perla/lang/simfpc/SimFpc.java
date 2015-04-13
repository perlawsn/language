package org.dei.perla.lang.simfpc;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Guido Rota 13/04/15.
 */
public class SimFpc implements Fpc {

    private final List<FpcAction> actions = new ArrayList<>();

    public synchronized List<FpcAction> getActions() {
        return new ArrayList<>(actions);
    }

    public synchronized void addAction(FpcAction action) {
        actions.add(action);
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
        return null;
    }

    @Override
    public Task set(Map<Attribute, Object> values, boolean strict, TaskHandler handler) {
        throw new UnsupportedOperationException(
                "'set' operation not supported by SimFpc");
    }

    @Override
    public Task get(List<Attribute> atts, boolean strict, TaskHandler handler) {
        return null;
    }

    @Override
    public Task get(List<Attribute> atts, boolean strict, long periodMs, TaskHandler handler) {
        return null;
    }

    @Override
    public Task async(List<Attribute> atts, boolean strict, TaskHandler handler) {
        return null;
    }

    @Override
    public void stop(Consumer<Fpc> handler) {

    }

}
