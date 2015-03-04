package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Not extends UnaryExpression {

    public Not(Expression e) {
        super(e);
    }

    @Override
    public Object doRun(Object o) {
        return !(Boolean) o;
    }

}
