package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class CastFloat implements Expression {

    private final Expression e;

    private CastFloat(Expression e) {
        this.e = e;
    }

    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t == DataType.FLOAT) {
            return e;
        }

        if (t != null && t != DataType.INTEGER) {
            return new ErrorExpression("Cannot cast " + t + " to float");
        }

        if (e instanceof Null || e instanceof ErrorExpression) {
            return e;
        }

        if (e instanceof Constant) {
            Integer value = (Integer) ((Constant) e).getValue();
            return new Constant(value.floatValue(), DataType.FLOAT);
        }

        return new CastFloat(e);
    }

    @Override
    public DataType getType() {
        return DataType.FLOAT;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        if (e.isComplete()) {
            return this;
        }
        return new CastFloat(e.rebuild(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return ((Integer) e.run(record, buffer)).floatValue();
    }

}
