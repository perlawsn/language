package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Xor extends BinaryExpression {

    public Xor(Expression e1, Expression e2) {
        super(e1, e2, DataType.BOOLEAN);
    }

    @Override
    public Object doRun(Object o1, Object o2) {
        Boolean b1 = (Boolean) o1;
        Boolean b2 = (Boolean) o2;
        return (b1 || b2) && !(b1 && b2);
    }

}
