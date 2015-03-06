package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Field implements Expression {

    private final DataType type;
    private final int idx;

    public Field(int idx, DataType type) {
        this.idx = idx;
        this.type = type;
    }

    @Override
    public DataType getType() {
        return type;
    }

    public int getIndex() {
        return idx;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return record[idx];
    }

}
