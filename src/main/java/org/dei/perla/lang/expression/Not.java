package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public class Not extends UnaryExpression {

    public Not(Expression e) {
        super(e, DataType.BOOLEAN);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        return !(Boolean) e.compute(record, buffer);
    }

}
