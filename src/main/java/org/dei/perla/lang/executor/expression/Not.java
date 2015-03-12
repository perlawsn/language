package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Not implements Expression {

    private final Expression e;

    private Not(Expression e) {
        this.e = e;
    }

    public static Expression create(Expression e) {
        if (e.getType() != null && e.getType() != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean values are allowed");
        }

        if (e instanceof Constant) {
            Boolean b = (Boolean) ((Constant) e).getValue();
            if (b == null) {
                return Constant.NULL_BOOLEAN;
            }
            return new Constant(!b, DataType.BOOLEAN);
        }

        return new Not(e);
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
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
        Boolean b = (Boolean) e.run(record, buffer);
        if (b == null) {
            return null;
        }
        return !b;
    }

}
