package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class CastFloat implements Expression {

    private final Expression e;
    private final DataType from;

    public CastFloat(Expression e) {
        this.e = e;
        this.from = e.getType();
    }

    @Override
    public DataType getType() {
        return DataType.FLOAT;
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Object res = e.compute(record, buffer);

        switch (from) {
        case INTEGER:
            return ((Integer) res).floatValue();
        case FLOAT:
            return res;
        default:
            return null;
        }
    }

}
