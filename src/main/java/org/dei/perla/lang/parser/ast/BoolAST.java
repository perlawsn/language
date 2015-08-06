package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Bool;
import org.dei.perla.lang.query.expression.BoolOperation;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Map;

/**
 * A boolean operation with 2 operands. The NOT binary operation is
 * represented in the Abstract Syntax Tree using the {@link NotAST} class.
 *
 * @author Guido Rota 30/07/15.
 */
public final class BoolAST extends BinaryExpressionAST {

    private final BoolOperation op;

    public BoolAST(BoolOperation op, ExpressionAST left, ExpressionAST right) {
        super(left, right);
        this.op = op;
    }

    public BoolAST(Token token, BoolOperation op, ExpressionAST left,
            ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public BoolOperation getOperation() {
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
        return left.inferType(bound, ctx) && right.inferType(bound, ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        Expression leftExp = left.toExpression(ctx, atts);
        Expression rightExp = right.toExpression(ctx, atts);

        if (leftExp instanceof Constant && rightExp instanceof Constant) {
            Object o1 = ((Constant) leftExp).getValue();
            Object o2 = ((Constant) rightExp).getValue();
            Object value = Bool.compute(op, o1, o2);
            return Constant.create(value, DataType.BOOLEAN);
        }

        return new Bool(op, leftExp, rightExp);
    }

}
