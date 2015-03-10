package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class BinaryExpression implements Expression {

    private final Expression e1;
    private final Expression e2;
    private final DataType type;

    protected BinaryExpression(Expression e1, Expression e2) {
        if (e1.getType() != e2.getType()) {
            throw new IllegalArgumentException("different argument types");
        }
        this.e1 = e1;
        this.e2 = e2;
        type = e1.getType();
    }

    protected abstract Expression create(Expression e1, Expression e2);

    @Override
    public final boolean isComplete() {
        return e1.isComplete() && e2.isComplete();
    }

    @Override
    public final boolean hasError() {
        return e1.hasError() || e2.hasError();
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public final Expression rebuild(List<Attribute> atts) {
        if (e1.isComplete() && e2.isComplete()) {
            return this;
        }
        Expression newE1 = e1.rebuild(atts);
        Expression newE2 = e2.rebuild(atts);
        return this.create(newE1, newE2);
    }

    @Override
    public final Object run(Object[] record, BufferView view) {
        Object o1 = e1.run(record, view);
        Object o2 = e2.run(record, view);
        if (o1 == null || o2 == null) {
            return null;
        }
        return doRun(type, o1, o2);
    }

    protected abstract Object doRun(DataType type, Object o1, Object o2);

}
