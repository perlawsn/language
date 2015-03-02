package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class BitwiseNot extends UnaryExpression {

    public BitwiseNot(Expression e) {
        super(e, DataType.INTEGER);
    }

    @Override
    protected Object doCompute(Object o) {
        return ~(Integer) o;
    }

}
