package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public class Or extends BinaryExpression {

    public Or(Expression e1, Expression e2) {
        super(e1, e2, DataType.BOOLEAN);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        Boolean b1 = (Boolean) e1.compute(record, buffer);
        Boolean b2 = (Boolean) e2.compute(record, buffer);
        return b1 || b2;
    }

}
