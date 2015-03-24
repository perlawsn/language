package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;
import java.util.Set;

/**
 * Expression for performing the bitwise complement of a single value.
 *
 * @author Guido Rota 27/02/15.
 */
public final class BitwiseNot implements Expression {

    private final Expression e;

    /**
     * Private constructor, new {@code BitwiseNot} instances must be
     * created using the static {@code create} method.
     */
    private BitwiseNot(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new bitwise complement expression
     *
     * @param e value to complement
     * @return new bitwise complement expression
     */
    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer values are allowed in bitwise not operations");
        }

        if (e instanceof Constant) {
            Object o = ((Constant) e).getValue();
            if (o == null) {
                return Constant.NULL_INTEGER;
            }
            return Constant.create(~(Integer) o, t);
        }

        return new BitwiseNot(e);
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
    public void getFields(Set<String> fields) {
        e.getFields(fields);
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
        if (o == null) {
            return null;
        }
        return ~(Integer) o;
    }

}
