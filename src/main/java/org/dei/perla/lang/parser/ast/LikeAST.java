package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.Like;

import java.util.Map;

/**
 * LIKE Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class LikeAST extends UnaryExpressionAST {

    private final String pattern;

    public LikeAST(ExpressionAST operand, String pattern) {
        this(null, operand, pattern);
    }

    public LikeAST(Token token, ExpressionAST operand, String pattern) {
        super(token, operand);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(DataType.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("LIKE", getPosition(),
                    bound.getType(), DataType.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        TypeVariable newBound = new TypeVariable(DataType.STRING);
        return operand.inferType(newBound, ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        Expression e = operand.toExpression(ctx, atts);

        if (e instanceof Constant) {
            Object o = Like.compute(((Constant) e).getValue(), pattern);
            return Constant.create(o, DataType.BOOLEAN);
        }

        return new Like(e, pattern);
    }

}
