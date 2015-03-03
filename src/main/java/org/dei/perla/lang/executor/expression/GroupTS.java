package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 03/03/15.
 */
public final class GroupTS implements Expression {

    private final int tsIdx;

    public GroupTS(int tsIdx) {
        this.tsIdx = tsIdx;
    }

    @Override
    public DataType getType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        return buffer.get(0)[tsIdx];
    }

}
