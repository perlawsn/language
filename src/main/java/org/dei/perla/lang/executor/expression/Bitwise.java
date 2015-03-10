package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 10/03/15.
 */
public final class Bitwise implements Expression {

    private final BitwiseOperator op;
    private final Expression e1;
    private final Expression e2;

    private Bitwise(BitwiseOperator op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public static Expression createAND(Expression e1, Expression e2) {
        return create(BitwiseOperator.AND, e1, e2);
    }

    public static Expression createOR(Expression e1, Expression e2) {
        return create(BitwiseOperator.OR, e1, e2);
    }

    public static Expression createXOR(Expression e1, Expression e2) {
        return create(BitwiseOperator.OR, e1, e2);
    }

    public static Expression createRSH(Expression e1, Expression e2) {
        return create(BitwiseOperator.RSH, e1, e2);
    }

    public static Expression createLSH(Expression e1, Expression e2) {
        return create(BitwiseOperator.LSH, e1, e2);
    }

    public static Expression create(BitwiseOperator op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.INTEGER ||
                t2 != null && t2 != DataType.INTEGER) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer operands are allowed in " + op + );
        }
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public boolean isComplete() {
        return e1.isComplete() && e2.isComplete();
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        return null;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return null;
    }

}
