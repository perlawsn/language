package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * An expression that performs a cast to float. This class only supports
 * float and integer operands.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CastFloat implements Expression {

    private final Expression e;

    /**
     * Private constructor, new {@code CastFloat} instances must be
     * created using the static {@code create} method.
     */
    private CastFloat(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new expression that performs an integer to float cast.
     *
     * @param e value to be cast to float
     * @return an expression that casts integer values to float
     */
    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t == DataType.FLOAT) {
            return e;
        }

        if (t != null && t != DataType.INTEGER) {
            return new ErrorExpression("Cannot cast " + t + " to float");
        }

        if (e instanceof Constant) {
            Integer value = (Integer) ((Constant) e).getValue();
            if (value == null) {
                return Constant.NULL_FLOAT;
            }
            return Constant.create(value.floatValue(), DataType.FLOAT);
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
    public boolean hasErrors() {
        return e.hasErrors();
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        if (e.isComplete()) {
            return this;
        }
        return CastFloat.create(e.bind(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return ((Integer) e.run(record, buffer)).floatValue();
    }

}
