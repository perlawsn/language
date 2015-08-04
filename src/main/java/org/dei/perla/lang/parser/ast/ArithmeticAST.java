package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.TypeClass;
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
        super(left, right);
        this.op = op;
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
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.NUMERIC);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.NUMERIC);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return left.inferType(bound, ctx) && right.inferType(bound, ctx);
    }

    @Override
    public Expression compile(ParserContext ctx, Map<String, Integer> atts) {
        Expression leftExp = left.compile(ctx, atts);
        Expression rightExp = right.compile(ctx, atts);
        DataType resType = getTypeClass().toDataType();

        if (leftExp instanceof Constant && rightExp instanceof Constant) {
            Object o1 = ((Constant) leftExp).getValue();
            Object o2 = ((Constant) rightExp).getValue();
            Object value;
            if (resType == DataType.INTEGER) {
                value = Arithmetic.computeInteger(op, o1, o2);
            } else if (resType == DataType.FLOAT) {
                value = Arithmetic.computeFloat(op, o1, o2);
            } else {
                throw new IllegalStateException("Illegal result type " +
                        resType + " found while copiling ArithmeticAST to " +
                        "concrete Arithmetic expression node");
            }
            return Constant.create(value, resType);
        }

        return new Arithmetic(op, leftExp, rightExp, resType);
    }

}
