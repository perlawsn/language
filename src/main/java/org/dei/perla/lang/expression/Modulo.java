package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Modulo extends BinaryExpression {

    public Modulo(Expression e1, Expression e2, DataType type) {
        super(e1, e2, type);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Object o1 = e1.compute(record, buffer);
        Object o2 = e2.compute(record, buffer);

        return (Integer) o1 % (Integer) o2;
    }

}
