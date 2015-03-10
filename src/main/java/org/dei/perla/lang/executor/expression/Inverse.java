package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Inverse extends UnaryExpression {

    public Inverse(Expression e) {
        super(e);
    }

    @Override
    public Expression create(Expression e) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer and float values are allowed");
        }

        // Defer to later rebuild if something's missing
        if (!e.isComplete()) {
            return new Inverse(e);
        }

        if (e instanceof Constant) {
            Object o = ((Constant) e).getValue();
            if (o == null) {
                return Null.INSTANCE;
            }
            return new Constant(doRun(t, o), t);
        }

        return new Inverse(e);
    }

    @Override
    public Object doRun(DataType type, Object o) {
        switch (type) {
            case INTEGER:
                return - (Integer) o;
            case FLOAT:
                return - (Float) o;
            default:
                throw new RuntimeException("unexpected type " + type);
        }
    }

}
