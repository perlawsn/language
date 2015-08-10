package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.WindowSize;

import java.util.Collections;

/**
 * Common base class for window size Abstract Syntax Tree node
 *
 * @author Guido Rota 10/08/15.
 */
public abstract class WindowSizeAST extends NodeAST {

    public WindowSizeAST() {
        super();
    }

    public WindowSizeAST(Token t) {
        super(t);
    }

    /**
     * Utility method employed to evaluate the value of an integer ConstantAST
     *
     * @param value constant to convert
     * @param ctx Context for the storage of intermediate parsing state
     * @return integer value of the {@link ConstantAST} node
     */
    protected static int evaluateConstant(ExpressionAST value,
            ParserContext ctx) {
        Expression e = value.compile(TypeClass.INTEGER, ctx,
                Collections.emptyMap());
        if (!(e instanceof Constant)) {
            throw new IllegalStateException("Parser bug found, WindowSize " +
                    "expression is not constant");
        }
        return (Integer) ((Constant) e).getValue();
    }

    public abstract WindowSize compile(ParserContext ctx);

}
