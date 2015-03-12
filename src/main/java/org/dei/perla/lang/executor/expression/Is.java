package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.parser.LogicValue;

import java.util.List;

/**
 * @author Guido Rota 12/03/15.
 */
public final class Is implements Expression {

    private final Expression e;
    private final LogicValue l;

    private Is(Expression e, LogicValue l) {
        this.e = e;
        this.l = l;
    }

    public static Expression create(Expression e, LogicValue l) {
        throw new RuntimeException("unimplemented");
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
        return create(e.rebuild(atts), l);
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return null;
    }
}
