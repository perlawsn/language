package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.SamplingEvent;

import java.util.*;

/**
 * Event based sampling clause Abstract Syntax Tree node
 *
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

    public SamplingEvent compile(ParserContext ctx) {
        Set<String> names = new HashSet<>();
        List<Attribute> atts = new ArrayList<>();
        for (String s : events) {
            if (names.contains(s)) {
                ctx.addError("Duplicate event '" + s + "' in sampling clause " +
                        "at " + getPosition());
                continue;
            }
            names.add(s);
            atts.add(Attribute.create(s, DataType.ANY));
        }
        return new SamplingEvent(atts);
    }

}
