package org.dei.perla.lang.expression;

import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Greater extends BinaryExpression {

    public Greater(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        return null;
    }

}
