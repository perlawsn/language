package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Between;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Map;

/**
 * Between Abstract Syntax Tree node. This operation is not allowed on
 * operands with type ID and BOOLEAN.
 *
 * @author Guido Rota 30/07/15.
 */
public final class BetweenAST extends ExpressionAST {

    private final ExpressionAST operand;
    private final ExpressionAST min;
    private final ExpressionAST max;

    public BetweenAST(ExpressionAST operand, ExpressionAST min,
            ExpressionAST max) {
        super();
        this.operand = operand;
        this.min = min;
        this.max = max;
    }

    public BetweenAST(Token token, ExpressionAST operand, ExpressionAST min,
            ExpressionAST max) {
        super(token);
        this.operand = operand;
        this.min = min;
        this.max = max;
    }

    public ExpressionAST getOperand() {
        return operand;
    }

    public ExpressionAST getMin() {
        return min;
    }

    public ExpressionAST getMax() {
        return max;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("BETWEEN", getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);

        TypeVariable newBound = new TypeVariable(TypeClass.ANY);
        res = operand.inferType(newBound, ctx) && min.inferType(newBound, ctx)
                && max.inferType(newBound, ctx);

        if (newBound.getTypeClass() == TypeClass.BOOLEAN ||
                newBound.getTypeClass() == TypeClass.ID) {
            String msg = "BOOLEAN and ID operand types are not compatible " +
                    "with BETWEEN operation at " + getPosition();
            ctx.addError(msg);
            res = false;
        }

        return res;
    }

    @Override
    public Expression compile(ParserContext ctx, Map<String, Integer> atts) {
        TypeClass t = operand.getTypeClass();
        if (t == TypeClass.ID || t == TypeClass.BOOLEAN) {
            String msg = "Comparison operation 'between' forbidden on " +
                    "arguments of type '" + t + "' at " + getPosition();
            ctx.addError(msg);
            return Constant.NULL;
        }

        Expression opExp = operand.compile(ctx, atts);
        Expression minExp = operand.compile(ctx, atts);
        Expression maxExp = operand.compile(ctx, atts);

        if (opExp instanceof Constant && minExp instanceof Constant &&
                maxExp instanceof Constant) {
            Object o = ((Constant) opExp).getValue();
            Object omin = ((Constant) minExp).getValue();
            Object omax = ((Constant) maxExp).getValue();
            Object value = Between.compute(o, omin, omax);
            return Constant.create(value, DataType.BOOLEAN);
        }

        return new Between(opExp, minExp, maxExp);
    }

}
