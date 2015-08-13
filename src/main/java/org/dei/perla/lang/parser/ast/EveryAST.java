package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

import java.time.temporal.TemporalUnit;

/**
 * @author Guido Rota 30/07/15.
 */
public final class EveryAST extends NodeAST {

    private final ExpressionAST value;
    private final TemporalUnit unit;

    public EveryAST(ExpressionAST value, TemporalUnit unit) {
        this(null, value, unit);
    }

    public EveryAST(Token token, ExpressionAST value, TemporalUnit unit) {
        super(token);
        this.value = value;
        this.unit = unit;
    }

    public ExpressionAST getValue() {
        return value;
    }

    public TemporalUnit unit() {
        return unit;
    }

}
