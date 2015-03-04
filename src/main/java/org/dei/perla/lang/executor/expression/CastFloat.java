package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class CastFloat implements Expression {

    private final Expression e;

    public CastFloat(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.FLOAT;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object res = e.run(record, buffer);

        switch (e.getType()) {
        case INTEGER:
            return ((Integer) res).floatValue();
        case FLOAT:
            return res;
        default:
            throw new RuntimeException("unexpected type " + e.getType());
        }
    }

}
