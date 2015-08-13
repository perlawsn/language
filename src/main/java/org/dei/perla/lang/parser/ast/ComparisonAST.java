package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.*;

import java.util.Map;

/**
 * Comparison Abstract Syntax Tree node. For data types ID and BOOLEAN the
 * only comparison operators allowed are EQ and NE.
 *
 * @author Guido Rota 30/07/15.
 */
public final class ComparisonAST extends BinaryExpressionAST {

    private final ComparisonOperation op;

    public ComparisonAST(ComparisonOperation op, ExpressionAST left,
            ExpressionAST right) {
        this(null, op, left, right);
    }

    public ComparisonAST(Token token, ComparisonOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public ComparisonOperation getOperation() {
        return op;
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        TypeVariable newBound = new TypeVariable(TypeClass.ANY);
        return left.inferType(newBound, ctx) && right.inferType(newBound, ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        TypeClass t = left.getTypeClass();
        if ((t == TypeClass.ID || t == TypeClass.BOOLEAN) &&
                op != ComparisonOperation.EQ && op != ComparisonOperation.NE) {
            String msg = "Comparison operation '" + op + "' forbidden on " +
                    "arguments of type '" + t + "' at " + getPosition();
            ctx.addError(msg);
            return Constant.NULL;
        }

        Expression leftExp = left.toExpression(ctx, atts);
        Expression rightExp = right.toExpression(ctx, atts);

        if (leftExp instanceof Constant && rightExp instanceof Constant) {
            Object o1 = ((Constant) leftExp).getValue();
            Object o2 = ((Constant) rightExp).getValue();
            Object value = Comparison.compute(op, o1, o2);
            return Constant.create(value, DataType.BOOLEAN);
        }

        return new Comparison(op, leftExp, rightExp);
    }

}
