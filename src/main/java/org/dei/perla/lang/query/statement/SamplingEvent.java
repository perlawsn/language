package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;

import java.util.Collections;
import java.util.List;

/**
 * Event-based sampling clause
 *
 * @author Guido Rota 30/03/15.
 */
public final class SamplingEvent implements Sampling {

    private final List<Attribute> events;

    public SamplingEvent(List<Attribute> events) {
        this.events = Collections.unmodifiableList(events);
    }

    public List<Attribute> getEvents() {
        return events;
    }

    public String toString(){
    	return "SAMPLING ON EVENT " + events;
    }
}
