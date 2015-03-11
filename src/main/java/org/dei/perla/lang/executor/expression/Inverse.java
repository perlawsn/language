package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Inverse implements Expression {

    private final Expression e;

    private Inverse(Expression e) {
        this.e = e;
    }

    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer and float values are allowed");
        }

        if (e instanceof Null) {
            return Null.INSTANCE;
        }

        if (e instanceof Constant) {
            Object o = ((Constant) e).getValue();
            if (o == null) {
                return Null.INSTANCE;
            }
            return new Constant(compute(t, o), t);
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
    public Expression rebuild(List<Attribute> atts) {
        if (e.isComplete()) {
            return this;
        }
        return create(e.rebuild(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o = e.run(record, buffer);
        if (o == null) {
            return null;
        }
        return compute(e.getType(), o);
    }

    public static Object compute(DataType type, Object o) {
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
