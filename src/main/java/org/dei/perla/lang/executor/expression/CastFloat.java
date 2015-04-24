package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An expression that performs a cast to float. This class only supports
 * float and integer operands.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CastFloat extends Expression {

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
     * @param err error tracking object
     * @return an expression that casts integer values to float
     */
    public static Expression create(Expression e, Errors err) {
        DataType t = e.getType();
        if (t == DataType.FLOAT) {
            return e;
        }

        if (t != null && t != DataType.INTEGER) {
            err.addError("Cannot cast " + t + " to float");
            return Constant.NULL;
        }

        if (e instanceof Constant) {
            Integer value = (Integer) ((Constant) e).getValue();
            if (value == null) {
                return Constant.NULL;
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
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.bind(atts, bound, err);
        return CastFloat.create(be, err);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        if (o == null) {
            return null;
        }

        return ((Integer) o).floatValue();
    }

}
