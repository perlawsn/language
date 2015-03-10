package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Not extends UnaryExpression {

    private Not(Expression e) {
        super(e);
    }

    @Override
    public Expression create(Expression e) {
        if (e.getType() != null && e.getType() != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean values are allowed");
        }

        // Defer to later rebuild if something's missing
        if (!e.isComplete()) {
            return new Not(e);
        }

        if (e instanceof Constant) {
            Bool b = (Bool) ((Constant) e).getValue();
            if (b == null) {
                return Null.INSTANCE;
            }
            return new Constant(!b, DataType.BOOLEAN);
        }

        return new Not(e);
    }

    @Override
    public Object doRun(DataType type, Object o) {
        return !(Bool) o;
    }

}
