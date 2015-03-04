package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Constant implements Expression {

    private final Object value;
    private final DataType type;

    public Constant(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return value;
    }

}
