package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 23/02/15.
 */
public class Sum implements Expression {

    private final Expression e1;
    private final Expression e2;

    private final DataType tRes;

    public Sum(Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();
        if (t1 != DataType.INTEGER && t1 != DataType.FLOAT ||
                t2 != DataType.INTEGER && t2 != DataType.FLOAT) {
            throw new IllegalArgumentException("sum operands may only be of " +
                    "type INTEGER or FLOAT");
        }

        if (t1 == DataType.FLOAT || t2 == DataType.FLOAT) {
            tRes = DataType.FLOAT;
        } else {
            tRes = DataType.INTEGER;
        }

        this.e1 = e1;
        this.e2 = e2;
    }

    @Override
    public DataType getType() {
        return tRes;
    }

    @Override
    public Object compute(int index, BufferView buffer) {
        return null;
    }

}
