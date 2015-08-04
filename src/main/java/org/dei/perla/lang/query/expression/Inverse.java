package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An arithmetic expression that inverts the sign of its operand.
 *
 * @author Guido Rota 27/02/15.
 */
public final class Inverse extends Expression {

    private final Expression e;

    /**
     * Arithmetic sign inversion expression node constructor
     */
    public Inverse(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return e.getType();
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        return compute(e.getType(), o);
    }

    public static Object compute(DataType type, Object o) {
        if (o == null) {
            return null;
        }

        switch (type) {
            case INTEGER:
                return - (Integer) o;
            case FLOAT:
                return - (Float) o;
            default:
                throw new RuntimeException("unexpected type " + type);
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("(-")
                .append(e)
                .append(")");
    }

}
