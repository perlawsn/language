package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An arithmetic expression that inverts the sign of its operand.
 *
 * @author Guido Rota 27/02/15.
 */
public final class Inverse extends Expression {

    private final Expression e;

    /**
     * Private constructor, new {@code Inverse} instances must be
     * created using the static {@code create} method.
     */
    private Inverse(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new arithmetic expression that inverts the sign of its operand
     *
     * @param e operand
     * @param err error tracking object
     * @return new arithmetic inversion expression
     */
    public static Expression create(Expression e, Errors err) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            err.addError("Incompatible operand type: only integer and float " +
                    "values are allowed");
            return Constant.NULL;
        }

        if (e instanceof Constant) {
            Object o = ((Constant) e).getValue();
            return Constant.create(compute(t, o), t);
        }

        return new Inverse(e);
    }

    @Override
    public DataType getType() {
        return e.getType();
    }

    @Override
    public boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.bind(atts, bound, err);
        return create(be, err);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        return compute(e.getType(), o);
    }

    public static Object compute(DataType type, Object o) {
        if (o == null) {
            return null;
        }

        switch (type) {
            case INTEGER:
                return - (Integer) o;
            case FLOAT:
                return - (Float) o;
            default:
                throw new RuntimeException("unexpected type " + type);
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("(-")
                .append(e)
                .append(")");
    }

}
