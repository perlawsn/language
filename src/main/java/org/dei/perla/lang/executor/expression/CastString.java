package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class CastString implements Expression {

    private final Expression e;

    public CastString(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.STRING;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        return new CastString(e.rebuild(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return e.run(record, buffer).toString();
    }
}
