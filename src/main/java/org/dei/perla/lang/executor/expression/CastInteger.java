package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * An expression that performs a cast to integer. This class only supports
 * float and integer operands.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CastInteger implements Expression {

    private final Expression e;

    /**
     * Private constructor, new {@code CastInteger} instances must be
     * created using the static {@code create} method.
     */
    private CastInteger(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new expression that performs a float to integer cast.
     *
     * @param e value to be cast to integer
     * @return an expression that casts float values to integer
     */
    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t == DataType.INTEGER) {
            return e;
        }

        if (t != null && t != DataType.FLOAT) {
            return new ErrorExpression("Cannot cast " + t + " to float");
        }

        if (e instanceof Constant) {
            Float value = (Float) ((Constant) e).getValue();
            if (value == null) {
                return Constant.NULL_INTEGER;
            }
            return Constant.create(value.intValue(), DataType.INTEGER);
        }

        return new CastInteger(e);
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
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
        return CastInteger.create(e.bind(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return ((Float) e.run(record, buffer)).intValue();
    }

}