package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Set;

/**
 * @author Guido Rota 24/02/15.
 */
public final class Average implements Expression {

    private final Expression exp;
    private final Expression cond;

    public Average(Expression exp, Expression cond) {
        this.exp = exp;
        this.cond = cond;
    }

    @Override
    public DataType getType() {
        return exp.getType();
    }

    @Override
    public Set<Attribute> attributes() {
        return exp.attributes();
    }

    @Override
    public Object compute(int index, BufferView buffer) {
        return null;
    }

}
