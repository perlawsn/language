package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 09/03/15.
 */
public abstract class BooleanBinaryExpression extends BinaryExpression {

    protected BooleanBinaryExpression(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public final Expression create(Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.BOOLEAN ||
                t2 != null && t2 != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean values are allowed");
        }

        // Defer to later rebuild if something's missing
        if (!e1.isComplete() || !e2.isComplete()) {
            return createNoChecks(e1, e2);
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Boolean b1 = (Boolean) ((Constant) e1).getValue();
            Boolean b2 = (Boolean) ((Constant) e2).getValue();
            if (b1 == null || b2 == null) {
                return Null.INSTANCE;
            }
            Object res = doRun(DataType.BOOLEAN, b1, b2);
            return new Constant(res, DataType.BOOLEAN);
        }

        return createNoChecks(e1, e2);
    }

    protected abstract Expression createNoChecks(Expression e1, Expression e2);

}
