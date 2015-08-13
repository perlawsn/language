package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.RefreshType;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class RefreshAST extends NodeAST {

    public static final RefreshAST NEVER = new RefreshAST();

    private final RefreshType type;
    private final List<String> events;
    private final ExpressionAST value;
    private final TemporalUnit unit;

    public RefreshAST(List<String> events) {
        this(null, events);
    }

    public RefreshAST(Token token, List<String> events) {
        super(token);
        type = RefreshType.EVENT;
        this.events = Collections.unmodifiableList(events);
        value = null;
        unit = null;
    }

    public RefreshAST(ExpressionAST value, TemporalUnit unit) {
        this(null, value, unit);
    }

    public RefreshAST(Token token, ExpressionAST value, TemporalUnit unit) {
        super(token);
        type = RefreshType.TIME;
        this.value = value;
        this.unit = unit;
        events = null;
    }

    private RefreshAST() {
        super(null);
        type = RefreshType.NEVER;
        events = null;
        value = null;
        unit = null;
    }

    public RefreshType getType() {
        return type;
    }

    public List<String> getEvents() {
        return events;
    }

    public ExpressionAST getDurationValue() {
        return value;
    }

    public TemporalUnit getDurationUnit() {
        return unit;
    }

    public Refresh compile(ParserContext ctx) {
        switch (type) {
            case NEVER:
                return Refresh.NEVER;
            case EVENT:
                return compileEvents(ctx);
            case TIME:
                return compileTime(ctx);
            default:
                throw new RuntimeException("Unknown refresh type " + type);
        }
    }

    private Refresh compileEvents(ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

    private Refresh compileTime(ParserContext ctx) {
        int v = value.evalIntConstant(ctx);
        if (v <= 0) {
            ctx.addError("Refresh duration at " + getPosition() + " " +
                    "cannot be less or equal to zero");
        }
        return new Refresh(Duration.of(v, unit));
    }

}
