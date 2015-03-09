package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Null implements Expression {

    // There can only be a single instance of the Null class
    public static final Null INSTANCE = new Null();

    private Null() {}

    @Override
    public DataType getType() {
        return null;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return null;
    }

}
