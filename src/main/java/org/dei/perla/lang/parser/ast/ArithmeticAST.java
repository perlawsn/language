package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Arithmetic;
import org.dei.perla.lang.query.expression.ArithmeticOperation;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Map;

/**
 * An arithmetic operation with 2 operands. The inverse operation is
 * represented in the Abstract Syntax Tree using the {@link InverseAST} class.
 *
 * @author Guido Rota 30/07/15.
 */
public final class ArithmeticAST extends BinaryExpressionAST {

    private final ArithmeticOperation op;

    public ArithmeticAST(ArithmeticOperation op, ExpressionAST left,
            ExpressionAST right) {
        this(null, op, left, right);
    }

    public ArithmeticAST(Token token, ArithmeticOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public ArithmeticOperation getOperation() {
        return op;
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(DataType.NUMERIC);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getType(), DataType.NUMERIC);
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
        DataType opType = getDataType();

        if (leftExp instanceof Constant && rightExp instanceof Constant) {
            Object o1 = ((Constant) leftExp).getValue();
            Object o2 = ((Constant) rightExp).getValue();
            Object value;
            if (opType == DataType.INTEGER) {
                value = Arithmetic.computeInteger(op, o1, o2);
            } else if (opType == DataType.FLOAT) {
                value = Arithmetic.computeFloat(op, o1, o2);
            } else {
                throw new IllegalStateException("Illegal result type " +
                        opType + " found while copiling ArithmeticAST to " +
                        "concrete Arithmetic expression node");
            }
            return Constant.create(value, opType);
        }

        return new Arithmetic(op, leftExp, rightExp, opType);
    }

}
