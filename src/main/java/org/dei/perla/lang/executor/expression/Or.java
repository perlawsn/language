package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Or extends BooleanBinaryExpression {

    public Or(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    protected Expression createNoChecks(Expression e1, Expression e2) {
        return new Or(e1, e2);
    }

    @Override
    public Object doRun(DataType type, Object o1, Object o2) {
        return (Boolean) o1 || (Boolean) o2;
    }

}
