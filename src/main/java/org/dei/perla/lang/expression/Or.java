package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Or extends BinaryExpression {

    public Or(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public Object doCompute(Object o1, Object o2) {
        return (Boolean) o1 || (Boolean) o2;
    }

}
