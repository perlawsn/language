package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public final class CastInt implements Expression {

    private final Expression e;

    public CastInt(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Object res = e.compute(record, buffer);

        switch (e.getType()) {
            case INTEGER:
                return res;
            case FLOAT:
                return ((Float) res).intValue();
            default:
                throw new RuntimeException("unexpected type " + e.getType());

        }
    }

}
