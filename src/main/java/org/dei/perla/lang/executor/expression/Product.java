package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Product extends BinaryExpression {

    public Product(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public Object doRun(Object o1, Object o2) {
        switch (type) {
            case INTEGER:
                return (Integer) o1 * (Integer) o2;
            case FLOAT:
                return (Float) o1 * (Float) o2;
            default:
                throw new RuntimeException("product not define for type " + type);
        }
    }

}
