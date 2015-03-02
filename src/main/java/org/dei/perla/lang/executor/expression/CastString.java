package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public final class CastString implements Expression {

    private final Expression e;

    public CastString(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        return e.compute(record, buffer).toString();
    }
}
