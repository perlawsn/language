package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Set;
import java.util.TreeSet;

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

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        return record[idx];
    }

}
