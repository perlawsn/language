package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Addition extends ArithmeticBinaryExpression {

    private Addition(Expression e1, Expression e2) {
        super(e1, e2);
    }

    @Override
    protected Expression createNoChecks(Expression e1, Expression e2) {
        return new Addition(e1, e2);
    }

    @Override
    public Object doRun(DataType type, Object o1, Object o2) {
        switch (type) {
            case INTEGER:
                return (Integer) o1 + (Integer) o2;
            case FLOAT:
                return (Float) o1 + (Float) o2;
            default:
                throw new RuntimeException("addition not defined for type " + type);
        }
    }

}
