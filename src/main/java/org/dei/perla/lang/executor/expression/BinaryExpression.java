package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class BinaryExpression implements Expression {

    protected final Expression e1;
    protected final Expression e2;
    protected final DataType type;

    protected BinaryExpression(Expression e1, Expression e2, DataType type) {
        if (e1.getType() != e2.getType()) {
            throw new IllegalArgumentException("different argument types");
        }
        this.e1 = e1;
        this.e2 = e2;
        this.type = type;
    }

    protected BinaryExpression(Expression e1, Expression e2) {
        if (e1.getType() != e2.getType()) {
            throw new IllegalArgumentException("different argument types");
        }
        this.e1 = e1;
        this.e2 = e2;
        type = e1.getType();
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public final Object run(Object[] record, BufferView view) {
        Object o1 = e1.run(record, view);
        Object o2 = e2.run(record, view);
        return doRun(o1, o2);
    }

    protected abstract Object doRun(Object o1, Object o2);

}