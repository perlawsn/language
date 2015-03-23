package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.time.Duration;
import java.util.List;

/**
 * @author Guido Rota 16/03/15.
 */
public final class Refresh implements Clause {

    private final Duration d;
    private final List<String> names;
    private final List<Attribute> events = null;

    public Refresh(Duration d) {
        this.d = d;
        names = null;
    }

    public Refresh(List<String> events) {
        this.names = events;
        d = null;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return d != null || events != null;
    }

    @Override
    public Refresh bind(List<Attribute> events) {
        throw new RuntimeException("unimplemented");
    }

    public Duration getDuration() {
        return d;
    }

    public List<Attribute> getEvents() {
        return events;
    }

}
