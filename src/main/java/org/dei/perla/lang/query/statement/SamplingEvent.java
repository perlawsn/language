package org.dei.perla.lang.query.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.Expression;

import java.util.*;

/**
 * @author Guido Rota 30/03/15.
 */
public final class SamplingEvent implements Sampling {

    private final Set<String> names;
    private final List<Attribute> events;

    public SamplingEvent(Set<String> events) {
        this(events, Collections.emptyList());
    }

    private SamplingEvent(Set<String> names, List<Attribute> events) {
        this.names = Collections.unmodifiableSet(names);
        this.events = Collections.unmodifiableList(events);
    }

    public List<Attribute> getEvents() {
        return events;
    }

    @Override
    public boolean isComplete() {
        return !events.isEmpty();
    }

    @Override
    public SamplingEvent bind(Collection<Attribute> atts, Errors err) {
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

        events = Collections.unmodifiableList(events);
        return new SamplingEvent(names, events);
    }

}
