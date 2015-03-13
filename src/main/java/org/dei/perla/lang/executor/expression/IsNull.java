package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 13/03/15.
 */
public final class IsNull implements Expression {

    private final Expression e;

    private IsNull(Expression e) {
        this.e = e;
    }

    public static Expression create(Expression e) {
        if (e instanceof Constant) {
            if (((Constant) e).getValue() == null) {
                return Constant.TRUE;
            } else {
                return Constant.FALSE;
            }
        }
        return new IsNull(e);
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
        Object o = e.run(record, buffer);
        if (o == null) {
            return Constant.TRUE;
        } else {
            return Constant.FALSE;
        }
    }

}
