package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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

}
