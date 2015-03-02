package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Inverse extends UnaryExpression {

    public Inverse(Expression e) {
        super(e);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Object o = e.compute(record, buffer);
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
