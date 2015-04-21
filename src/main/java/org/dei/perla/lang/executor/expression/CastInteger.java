package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An expression that performs a cast to integer. This class only supports
 * float and integer operands.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CastInteger extends Expression {

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
     * @param err error tracking object
     * @return an expression that casts float values to integer
     */
    public static Expression create(Expression e, Errors err) {
        DataType t = e.getType();
        if (t == DataType.INTEGER) {
            return e;
        }

        if (t != null && t != DataType.FLOAT) {
            err.addError("Cannot cast " + t + " to float");
            return Constant.NULL;
        }

        if (e instanceof Constant) {
            Float value = (Float) ((Constant) e).getValue();
            if (value == null) {
                return Constant.NULL;
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
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.bind(atts, bound, err);
        return CastInteger.create(be, err);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        if (o == null) {
            return null;
        }
        return ((Float) o).intValue();
    }

}
