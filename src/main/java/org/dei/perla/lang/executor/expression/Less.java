package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Less extends BinaryExpression {

    public Less(Expression e1, Expression e2) {
        super(e1, e2, DataType.BOOLEAN);
    }

    @Override
    protected Object doRun(Object o1, Object o2) {
        switch (e1.getType()) {
            case INTEGER:
            case FLOAT:
            case STRING:
            case TIMESTAMP:
                @SuppressWarnings("unchecked")
                Comparable<Object> c1 = (Comparable<Object>) o1;
                return c1.compareTo(o2) < 0;
            default:
                throw new RuntimeException(
                        "greater comparison not defined for type " + type);

        }
    }

}
