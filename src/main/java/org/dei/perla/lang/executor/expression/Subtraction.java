package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Subtraction extends ArithmeticBinaryExpression {

    private Subtraction(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    protected Expression createNoChecks(Expression e1, Expression e2) {
        return new Subtraction(e1, e2);
    }

    @Override
    public Object doRun(DataType type, Object o1, Object o2) {
        switch (type) {
            case INTEGER:
                return (Integer) o1 - (Integer) o2;
            case FLOAT:
                return (Float) o1 - (Float) o2;
            default:
                throw new RuntimeException("subtraction not defined for type " + type);
        }
    }

}
