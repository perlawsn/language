package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Equal extends BinaryExpression {

    public Equal(Expression e1, Expression e2) {
        super(e1, e2, DataType.BOOLEAN);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Object o1 = e1.compute(record, buffer);
        Object o2 = e2.compute(record, buffer);

        return o1.equals(o2);
    }

}
