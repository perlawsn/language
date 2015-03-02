package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class BitwiseShiftR extends BinaryExpression {

    public BitwiseShiftR(Expression e1, Expression e2) {
        super(e1, e2, DataType.INTEGER);
    }

    @Override
    protected Object doCompute(Object o1, Object o2) {
        return (Integer) o1 >> (Integer) o2;
    }

}
