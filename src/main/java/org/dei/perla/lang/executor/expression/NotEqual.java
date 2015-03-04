package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class NotEqual extends BinaryExpression {

    public NotEqual(Expression e1, Expression e2) {
        super(e1, e2, DataType.BOOLEAN);
    }

    @Override
    protected Object doRun(Object o1, Object o2) {
        return !o1.equals(o2);
    }

}