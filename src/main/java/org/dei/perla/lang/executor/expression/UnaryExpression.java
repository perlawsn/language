package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class UnaryExpression implements Expression {

    private final Expression e;
    private final DataType type;

    protected UnaryExpression(Expression e) {
        this.e = e;
        this.type = e.getType();
    }

    public abstract Expression create(Expression e);

    @Override
    public final boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public final boolean hasError() {
        return e.hasError();
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public final Expression rebuild(List<Attribute> atts) {
        if (e.isComplete()) {
            return this;
        }
        Expression n = e.rebuild(atts);
        return this.create(e);
    }

    @Override
    public final Object run(Object[] record, BufferView buffer) {
        Object o = e.run(record, buffer);
        if (o == null) {
            return null;
        }
        return doRun(type, o);
    }

    protected abstract Object doRun(DataType type, Object o);

}
