package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 03/03/15.
 */
public final class GroupTS implements Expression {

    private final int tsIdx;

    private GroupTS(int tsIdx) {
        this.tsIdx = tsIdx;
    }

    @Override
    public DataType getType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean hasError() {
        return false;
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        return null;
    }

    public int getIndex() {
        return tsIdx;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return buffer.get(0)[tsIdx];
    }

}
