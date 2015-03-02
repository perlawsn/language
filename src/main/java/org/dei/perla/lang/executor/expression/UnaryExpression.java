package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class UnaryExpression implements Expression {

    protected final Expression e;
    protected final DataType type;

    protected UnaryExpression(Expression e, DataType type) {
        this.e = e;
        this.type = type;
    }

    protected UnaryExpression(Expression e) {
        this.e = e;
        this.type = e.getType();
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public final Object compute(Object[] record, BufferView buffer) {
        return doCompute(e.compute(record, buffer));
    }

    protected abstract Object doCompute(Object o);

}
