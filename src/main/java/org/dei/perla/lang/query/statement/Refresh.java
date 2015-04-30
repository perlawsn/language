package org.dei.perla.lang.query.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.query.expression.Expression;

import java.time.Duration;
import java.util.*;

/**
 * @author Guido Rota 16/03/15.
 */
public final class Refresh {

    public static final Refresh NEVER = new Refresh();

    private final RefreshType type;
    private final Duration d;
    private final Set<String> names;
    private final List<Attribute> events;

    private Refresh() {
        type = RefreshType.NEVER;
        d = null;
        names = null;
        events = null;
    }

    public Refresh(Duration d) {
        this.d = d;
        names = Collections.emptySet();
        events = Collections.emptyList();
        type = RefreshType.TIME;
    }

    public Refresh(Set<String> events) {
        this.names = Collections.unmodifiableSet(events);
        this.events = Collections.emptyList();
        d = null;
        type = RefreshType.EVENT;
    }

    private Refresh(Set<String> names, List<Attribute> events) {
        d = null;
        this.names = Collections.unmodifiableSet(names);
        this.events = Collections.unmodifiableList(events);
        type = RefreshType.EVENT;
    }

    public Duration getDuration() {
        if (type != RefreshType.TIME) {
            throw new RuntimeException(
                    "Cannot access duration in event-based Refresh object");
        }
        return d;
    }

    public List<Attribute> getEvents() {
        if (type != RefreshType.EVENT) {
            throw new RuntimeException(
                    "Cannot access events in time-based Refresh object");
        }
        return events;
    }

    public RefreshType getType() {
        return type;
    }

    public boolean isComplete() {
        if (names != null) {
            return events != null && names.size() == events.size();
        }
        return true;
    }

    public Refresh bind(Collection<Attribute> atts) {
        if (names == null) {
            return this;
        }

        List<Attribute> events = new ArrayList<>();
        for (String e : names) {
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

    /**
     * @author Guido Rota 30/03/2015
     */
    public static enum RefreshType {
        EVENT,
        TIME,
        NEVER
    }

}