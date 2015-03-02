package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Modulo extends BinaryExpression {

    public Modulo(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public Object doCompute(Object o1, Object o2) {
        return (Integer) o1 % (Integer) o2;
    }

}
