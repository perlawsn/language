package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Subtraction extends BinaryExpression {

    public Subtraction(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public Object doCompute(Object o1, Object o2) {
        switch (type) {
            case INTEGER:
                return (Integer) o1 - (Integer) o2;
            case FLOAT:
                return (Float) o1 - (Float) o2;
            default:
                throw new RuntimeException("subtraction not define for type " + type);
        }
    }

}
