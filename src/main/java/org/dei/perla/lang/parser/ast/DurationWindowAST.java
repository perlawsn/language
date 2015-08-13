package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.WindowSize;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

/**
 * Duration-based Abstract Syntax Tree node
 *
 * @author Guido Rota 10/08/15.
 */
public final class DurationWindowAST extends WindowSizeAST {

    private final ExpressionAST value;
    private final TemporalUnit unit;

    public DurationWindowAST(Token t, ExpressionAST value, TemporalUnit unit) {
        super(t);
        this.value = value;
        this.unit = unit;
    }

    public DurationWindowAST(ExpressionAST value, TemporalUnit unit) {
        super();
        this.value = value;
        this.unit = unit;
    }

    public ExpressionAST getValue() {
        return value;
    }

    public TemporalUnit getUnit() {
        return unit;
    }

    @Override
    public WindowSize compile(ParserContext ctx) {
        int v = evaluateConstant(value, ctx);
        if (v <= 0) {
            ctx.addError("Window size duration at " + getPosition() +
                    " cannot be less or equal to zero");
        }
        return new WindowSize(Duration.of(v, unit));
    }

}
