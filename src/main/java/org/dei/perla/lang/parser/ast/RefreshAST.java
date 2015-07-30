package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.RefreshType;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class RefreshAST extends NodeAST {

    public static final RefreshAST NEVER = new RefreshAST();

    private final RefreshType type;
    private final List<String> events;
    private final Duration duration;

    public RefreshAST(Token token, List<String> events) {
        super(token);
        type = RefreshType.EVENT;
        this.events = Collections.unmodifiableList(events);
        duration = null;
    }

    public RefreshAST(Token token, Duration duration) {
        super(token);
        type = RefreshType.TIME;
        this.duration = duration;
        events = null;
    }

    private RefreshAST() {
        super(null);
        type = RefreshType.NEVER;
        events = null;
        duration = null;
    }

    public RefreshType getRefreshType() {
        return type;
    }

    public List<String> getEvents() {
        if (type != RefreshType.EVENT) {
            throw new RuntimeException(
                    "No events in " + type.name() + " refresh");
        }
        return events;
    }

    public Duration getDuration() {
        if (type != RefreshType.TIME) {
            throw new RuntimeException(
                    "No duration in " + type.name() + " refresh");
        }
        return duration;
    }

}
