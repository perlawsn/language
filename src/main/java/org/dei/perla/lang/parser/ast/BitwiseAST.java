package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Bitwise;
import org.dei.perla.lang.query.expression.BitwiseOperation;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;


/**
 * A bitwise operation with 2 operands. The bitwise complement operation is
 * represented in the Abstract Syntax Tree using the {@link BitwiseNotAST} class.
 *
 * @author Guido Rota 30/07/15.
 */
public final class BitwiseAST extends BinaryExpressionAST {

    private final BitwiseOperation op;

    public BitwiseAST(BitwiseOperation op, ExpressionAST left,
            ExpressionAST right) {
        this(null, op, left, right);
    }

    public BitwiseAST(Token token, BitwiseOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public BitwiseOperation getOperation() {
        return op;
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(DataType.INTEGER);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getType(), DataType.INTEGER);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return left.inferType(bound, ctx) && right.inferType(bound, ctx);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, AttributeOrder ord) {
        Expression leftExp = left.toExpression(ctx, ord);
        Expression rightExp = right.toExpression(ctx, ord);

        if (leftExp instanceof Constant && rightExp instanceof Constant) {
            Object o1 = ((Constant) leftExp).getValue();
            Object o2 = ((Constant) rightExp).getValue();
            Object value = Bitwise.compute(op, o1, o2);
            return Constant.create(value, DataType.INTEGER);
        }

        return new Bitwise(op, leftExp, rightExp);
    }

}
