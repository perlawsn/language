package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Xor extends BooleanBinaryExpression {

    public Xor(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    protected Expression createNoChecks(Expression e1, Expression e2) {
        return new Xor(e1, e2);
    }

    @Override
    public Object doRun(DataType type, Object o1, Object o2) {
        Boolean b1 = (Boolean) o1;
        Boolean b2 = (Boolean) o2;
        return (b1 || b2) && !(b1 && b2);
    }

}
