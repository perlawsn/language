package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Constant implements Expression {

    private final DataType type;
    private final Object value;

    public Constant(DataType type, Object value) {
        // TODO: add congruency test between type and value?
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public Object compute(int index, BufferView buffer) {
        return value;
    }

}
