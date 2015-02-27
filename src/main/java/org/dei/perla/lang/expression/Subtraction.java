package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Subtraction extends BinaryExpression {

    public Subtraction(Expression e1, Expression e2, DataType type) {
        super(e1, e2, type);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Object o1 = e1.compute(record, buffer);
        Object o2 = e2.compute(record, buffer);

        switch (type) {
            case INTEGER:
                return (Integer) o1 - (Integer) o2;
            case FLOAT:
                return (Float) o1 - (Float) o2;
            default:
                throw new RuntimeException("sum not define for type " + type);
        }
    }

}
