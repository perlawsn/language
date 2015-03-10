package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Modulo extends BinaryExpression {

    private Modulo(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public final Expression create(Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.INTEGER ||
                t2 != null && t2 != DataType.INTEGER) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer is allowed in modulo operations");
        }

        // Defer to later rebuild if something's missing
        if (!e1.isComplete() || !e2.isComplete()) {
            return new Modulo(e1, e2);
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Integer i1 = (Integer) ((Constant) e1).getValue();
            Integer i2 = (Integer) ((Constant) e2).getValue();
            if (i1 == null || i2 == null) {
                return Null.INSTANCE;
            }
            return new Constant(i1 % i2, DataType.INTEGER);
        }

        return new Modulo(e1, e2);
    }

    @Override
    public Object doRun(DataType type, Object o1, Object o2) {
        return (Integer) o1 % (Integer) o2;
    }

}
