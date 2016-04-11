package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.WindowSize;
import org.dei.perla.lang.query.statement.WindowSize.WindowType;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

/**
 * WindowSize Abstract Syntax Tree node
 *
 * @author Guido Rota 10/08/15.
 */
public final class WindowSizeAST extends NodeAST {

    public final static WindowSizeAST ZERO =
            new WindowSizeAST(ConstantAST.ZERO);
    public final static WindowSizeAST ONE =
            new WindowSizeAST(ConstantAST.ONE);

    private final WindowType type;

    // Sample-based window size
    private final ExpressionAST samples;

    // Duration-based window size
    private final ExpressionAST value;
    private final TemporalUnit unit;


    public WindowSizeAST(ExpressionAST samples) {
        this(null, samples);
    }

    public WindowSizeAST(Token t, ExpressionAST samples) {
        super(t);
        type = WindowType.SAMPLE;
        
        this.samples = samples;
        
        value = null;
        unit = null;
    }

    public WindowSizeAST(ExpressionAST value, TemporalUnit unit) {
        this(null, value, unit);
    }

    public WindowSizeAST(Token t, ExpressionAST value, TemporalUnit unit) {
        super(t);
        type = WindowType.TIME;
        this.value = value;
        this.unit = unit;
        samples = null;
    }

    public WindowType getType() {
        return type;
    }

    public ExpressionAST getSamples() {
        return samples;
    }

    public ExpressionAST getDurationValue() {
        return value;
    }

    public TemporalUnit getDurationUnit() {
        return unit;
    }

    public WindowSize compile(ParserContext ctx) {
        switch (type) {
            case TIME:
                return compileDuration(ctx);
            case SAMPLE:
                return compileSamples(ctx);
            default:
                throw new RuntimeException("Unexpected WindowSize type " +
                        type);
        }
    }

    private WindowSize compileDuration(ParserContext ctx) {
        int v = value.evalIntConstant(ctx);
        if (v <= 0) {
            ctx.addError("Window size duration at " + getPosition() +
                    " cannot be less or equal to zero");
        }
        return new WindowSize(Duration.of(v, unit));
    }

    private WindowSize compileSamples(ParserContext ctx) {
        int s = samples.evalIntConstant(ctx);
        if (s <= 0) {
            ctx.addError("Window size sample count at " + getPosition() +
                    " cannot be less or equal to zero");
        }
        return new WindowSize(s);
    }

}
