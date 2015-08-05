package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.Is;
import org.dei.perla.lang.query.expression.LogicValue;

import java.util.Map;

/**
 * IS Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class IsAST extends UnaryExpressionAST {

    private final LogicValue value;

    public IsAST(ExpressionAST operand, LogicValue value) {
        super(operand);
        this.value = value;
    }

    public IsAST(Token token, ExpressionAST operand, LogicValue value) {
        super(token, operand);
        this.value = value;
    }

    public LogicValue getLogicValue() {
        return value;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("IS", getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return operand.inferType(bound, ctx);
    }

    @Override
    public Expression compile(ParserContext ctx, Map<String, Integer> atts) {
        Expression e = operand.compile(null, atts);
        if (e instanceof Constant) {
            LogicValue l = Is.compute(((Constant) e).getValue(), value);
            return Constant.create(l, DataType.BOOLEAN);
        }

        return new Is(e, value);
    }

}
