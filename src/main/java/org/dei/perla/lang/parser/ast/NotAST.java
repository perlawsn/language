package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.expression.Not;

import java.util.Map;

/**
 * Boolean NOT Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class NotAST extends UnaryExpressionAST {

    public NotAST(ExpressionAST operand) {
        super(operand);
    }

    public NotAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("not", getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return operand.inferType(bound, ctx);
    }

    @Override
    public Expression compile(ParserContext ctx, Map<String, Integer> atts) {
        Expression opExp = operand.compile(ctx, atts);

        if (opExp instanceof Constant) {
            LogicValue l = (LogicValue) ((Constant) opExp).getValue();
            return Constant.create(LogicValue.not(l), DataType.BOOLEAN);
        }

        return new Not(opExp);
    }

}
