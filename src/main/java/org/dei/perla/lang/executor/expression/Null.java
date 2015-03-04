package org.dei.perla.lang.executor.expression;

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
    public Object run(Object[] record, BufferView buffer) {
        return null;
    }

}
