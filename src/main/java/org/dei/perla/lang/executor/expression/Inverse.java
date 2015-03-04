package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Inverse extends UnaryExpression {

    public Inverse(Expression e) {
        super(e);
    }

    @Override
    public Object doRun(Object o) {
        switch (type) {
            case INTEGER:
                return - (Integer) o;
            case FLOAT:
                return - (Float) o;
            default:
                throw new RuntimeException("unexpected type " + type);
        }
    }

}
