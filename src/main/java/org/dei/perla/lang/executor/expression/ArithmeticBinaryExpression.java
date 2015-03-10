package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 09/03/15.
 */
public abstract class ArithmeticBinaryExpression extends BinaryExpression {

    protected ArithmeticBinaryExpression(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public final Expression create(Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.INTEGER && t1 != DataType.FLOAT ||
                t2 != null && t2 != DataType.INTEGER && t2 != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer and float operands are allowed");
        }

        // Defer to later rebuild if something's missing
        if (!e1.isComplete() || !e2.isComplete()) {
            return createNoChecks(e1, e2);
        }

        if (t1 != t2) {
            if (t1 == DataType.INTEGER) {
                e1 = new CastFloat(e1);
            } else {
                e2 = new CastFloat(e2);
            }
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            if (o1 == null || o2 == null) {
                return Null.INSTANCE;
            }
            return new Constant(doRun(e1.getType(), o1, o2), e1.getType());
        }

        return createNoChecks(e1, e2);
    }

    protected abstract Expression createNoChecks(Expression e1, Expression e2);

}
