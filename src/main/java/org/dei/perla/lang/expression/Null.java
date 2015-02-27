package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Null implements Expression {

    @Override
    public DataType getType() {
        return null;
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        return null;
    }

}
