package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.util.*;

/**
 * @author Guido Rota 16/03/15.
 */
public final class Refresh implements Clause {

    private final Duration d;
    private final Set<String> names;
    private final List<Attribute> events;

    public Refresh(Duration d) {
        this.d = d;
        names = null;
        events = Collections.emptyList();
    }

    public Refresh(Set<String> events) {
        this.names = Collections.unmodifiableSet(events);
        this.events = Collections.emptyList();
        d = null;
    }

    private Refresh(Set<String> names, List<Attribute> events) {
        d = null;
        this.names = names;
        this.events = events;
    }

    public Duration getDuration() {
        return d;
    }

    public List<Attribute> getEvents() {
        return events;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public boolean isComplete() {
        if (names == null) {
            return d != null;
        } else {
            return events != null && names.size() == events.size();
        }
    }

    public Refresh bind(Collection<Attribute> atts) {
        if (names == null) {
            return this;
        }

        List<Attribute> events = new ArrayList<>();
        for (String e : names) {
            int i = Expression.indexOf(e, events);
            if (i > 0) {
                events.add(events.get(i));
            }

            Attribute a = Expression.getById(e, atts);
            if (a == null) {
                continue;
            }
            events.add(a);
        }

        if (events.size() == 0) {
            return this;
        }

        return new Refresh(names, events);
    }

}
