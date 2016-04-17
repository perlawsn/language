package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * Refresh clause
 *
 * @author Guido Rota 16/03/15.
 */
public final class Refresh {

    public static final Refresh NEVER = new Refresh();

    private final RefreshType type;
    private final Duration d;
    private final List<Attribute> events;

    private Refresh() {
        type = RefreshType.NEVER;
        d = null;
        events = null;
    }

    public Refresh(Duration d) {
        this.d = d;
        events = Collections.emptyList();
        type = RefreshType.TIME;
    }

    public Refresh(List<Attribute> events) {
        d = null;
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

    public String toString(){
    	if(type == RefreshType.TIME)
    		return d.toString();
    	else if(type == RefreshType.EVENT)
    		return events.toString();
    	else if (this == Refresh.NEVER)
    		return "NEVER";
    	return " ";
    }
}
