package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * Sample-based window size Abstract Syntax Tree node
 *
 * @author Guido Rota 10/08/15.
 */
public final class SampleWindowAST extends WindowSizeAST {

    private final ExpressionAST samples;

    public SampleWindowAST(Token t, ExpressionAST samples) {
        super(t);
        this.samples = samples;
    }

    public SampleWindowAST(ExpressionAST samples) {
        super();
        this.samples = samples;
    }

    public ExpressionAST getSamples() {
        return samples;
    }

    @Override
    public WindowSize compile(ParserContext ctx) {
        int s = evaluateConstant(samples, ctx);
        if (s <= 0) {
            ctx.addError("Window size sample count at " + getPosition() +
                    " cannot be less or equal to zero");
        }
        return new WindowSize(s);
    }

}
