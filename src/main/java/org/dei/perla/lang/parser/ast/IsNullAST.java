package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.IsNull;
import org.dei.perla.lang.query.expression.LogicValue;

/**
 * IS NULL Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class IsNullAST extends UnaryExpressionAST {

    public IsNullAST(ExpressionAST operand) {
        this(null, operand);
    }

    public IsNullAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(DataType.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("IS NULL", getPosition(),
                    bound.getType(), DataType.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return operand.inferType(new TypeVariable(DataType.ANY), ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, AttributeOrder ord) {
        Expression e = operand.toExpression(null, ord);
        if (e instanceof Constant) {
            LogicValue l = IsNull.compute(((Constant) e).getValue());
            return Constant.create(l, DataType.BOOLEAN);
        }
        return new IsNull(e);
    }

}
