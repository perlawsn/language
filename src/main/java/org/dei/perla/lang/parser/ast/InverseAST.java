package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.Inverse;

import java.util.Map;

/**
 * Arithmetic inverse Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class InverseAST extends UnaryExpressionAST {

    public InverseAST(ExpressionAST operand) {
        this(null, operand);
    }

    public InverseAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(DataType.NUMERIC);
        if (!res) {
            String msg = typeErrorString("-", getPosition(),
                    bound.getType(), DataType.NUMERIC);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return operand.inferType(bound, ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        Expression opExp = operand.toExpression(ctx, atts);
        DataType opType = getDataType();

        if (opExp instanceof Constant) {
            Object o = ((Constant) opExp).getValue();
            return Constant.create(Inverse.compute(opType, o), opType);
        }

        return new Inverse(opExp, opType);
    }

}
