package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class CastInt implements Expression {

    private final Expression e;

    public CastInt(Expression e) {
        this.e = e;
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
    public Expression rebuild(List<Attribute> atts) {
        return new CastInt(e.rebuild(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object res = e.run(record, buffer);

        switch (e.getType()) {
            case INTEGER:
                return res;
            case FLOAT:
                return ((Float) res).intValue();
            default:
                throw new RuntimeException("unexpected type " + e.getType());

        }
    }

}
