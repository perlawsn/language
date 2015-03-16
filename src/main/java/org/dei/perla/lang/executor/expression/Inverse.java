package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * An arithmetic expression that inverts the sign of its operand.
 *
 * @author Guido Rota 27/02/15.
 */
public final class Inverse implements Expression {

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
     * @return new arithmetic inversion expression
     */
    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer and float values are allowed");
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
    public boolean hasErrors() {
        return e.hasErrors();
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        if (e.isComplete()) {
            return this;
        }
        return create(e.bind(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o = e.run(record, buffer);
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

}
