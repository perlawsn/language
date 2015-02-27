package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 27/02/15.
 */
public class Arithmetic extends BinaryExpression {

    private final Operation op;

    public Arithmetic(Operation op, Expression e1, Expression e2,
            DataType type) {
        super(e1, e2, type);
        this.op = op;
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        if (type == null) {
            return null;
        }

        Object o1 = e1.compute(record, buffer);
        Object o2 = e2.compute(record, buffer);

        switch (type) {
            case INTEGER:
                return computeInt((Integer) o1, (Integer) o2);
            case FLOAT:
                return computeFloat((Float) o1, (Float) o2);
            default:
                return null;
        }
    }

    private Object computeInt(Integer o1, Integer o2) {
        switch (op) {
            case SUM:
                return o1 + o2;
            case SUBTRACTION:
                return o1 - o2;
            case PRODUCT:
                return o1 * o2;
            case DIVISION:
                return o1 / o2;
            case MODULO:
                return o1 % o2;
            default:
                return null;
        }
    }

    private Object computeFloat(Float o1, Float o2) {
        switch (op) {
            case SUM:
                return o1 + o2;
            case SUBTRACTION:
                return o1 - o2;
            case PRODUCT:
                return o1 * o2;
            case DIVISION:
                return o1 / o2;
            case MODULO:
                throw new RuntimeException("undefined float modulo");
            default:
                return null;
        }
    }

    public enum Operation {
        SUM,
        SUBTRACTION,
        PRODUCT,
        DIVISION,
        MODULO
    }

}
