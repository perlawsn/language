package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Guido Rota 16/03/15.
 */
public final class Refresh implements Clause {

    private final Duration d;
    private final Set<String> names;
    private final List<Attribute> events = null;

    public Refresh(Duration d) {
        this.d = d;
        names = null;
    }

    public Refresh(Set<String> events) {
        this.names = Collections.unmodifiableSet(events);
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
    public void getFields(Set<String> fields) {
        if (names == null) {
            return;
        }
        fields.addAll(names);
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
