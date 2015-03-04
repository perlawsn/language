package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Modulo extends BinaryExpression {

    public Modulo(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    public Object doRun(Object o1, Object o2) {
        return (Integer) o1 % (Integer) o2;
    }

}
