package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class BitwiseNot implements Expression {

    private final Expression e;

    private BitwiseNot(Expression e) {
        this.e = e;
    }

    public static Expression create(Expression e) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer values are allowed in bitwise not operations");
        }

        if (e instanceof Null) {
            return Null.INSTANCE;
        }
        if (e instanceof ErrorExpression) {
            return e;
        }

        if (e instanceof Constant) {
            Object o = ((Constant) e).getValue();
            if (o == null) {
                return Null.INSTANCE;
            }
            return new Constant(~(Integer) o, t);
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
        return ~(Integer) o;
    }

}
