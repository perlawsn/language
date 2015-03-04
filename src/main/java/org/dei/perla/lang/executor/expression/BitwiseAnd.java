package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class BitwiseAnd extends BinaryExpression {

    public BitwiseAnd(Expression e1, Expression e2) {
        super(e1, e2, DataType.INTEGER);
    }

    @Override
    protected Object doRun(Object o1, Object o2) {
        return (Integer) o1 & (Integer) o2;
    }

}
