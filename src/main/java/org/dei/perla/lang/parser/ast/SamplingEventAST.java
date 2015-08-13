package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class SamplingEventAST extends SamplingAST {

    private final List<String> events;

    public SamplingEventAST(List<String> events) {
        this(null, events);
    }

    public SamplingEventAST(Token token, List<String> events) {
        super(token);
        this.events = Collections.unmodifiableList(events);
    }

    public List<String> getEvents() {
        return events;
    }

}
