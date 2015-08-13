package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.BitwiseNot;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Map;

/**
 * Bitwise complement Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class BitwiseNotAST extends UnaryExpressionAST {

    public BitwiseNotAST(ExpressionAST operand) {
        this(null, operand);
    }

    public BitwiseNotAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.INTEGER);
        if (!res) {
            String msg = typeErrorString("~", getPosition(),
                    bound.getTypeClass(), TypeClass.INTEGER);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return operand.inferType(bound, ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        Expression opExp = operand.toExpression(ctx, atts);

        if (opExp instanceof Constant) {
            Integer i = (Integer) ((Constant) opExp).getValue();
            return Constant.create(~i, DataType.INTEGER);
        }

        return new BitwiseNot(opExp);
    }

}
