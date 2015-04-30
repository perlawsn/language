package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * A special {@code Expression} node that retrieves the GROUP TIMESTAMP of a
 * buffer portion resulting from a time-based grouping.
 *
 * <p>
 * Newly created {@code GroupTS} objects must be bound to an actual attribute
 * list before being used.
 *
 * @author Guido Rota 03/03/15.
 */
public class GroupTS extends Expression {

    private static final String id = Attribute.TIMESTAMP.getId();

    @Override
    public DataType getType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        int i = Expression.indexOf(id, bound);
        if (i != -1) {
            return new ConcreteGroupTS(i);
        }

        Attribute a = Expression.getById(id, atts);
        if (a == null) {
            return this;
        }

        bound.add(a);
        return new ConcreteGroupTS(bound.size() - 1);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        return null;
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("timestamp");
    }

    private static final class ConcreteGroupTS extends GroupTS {

        private final int tsIdx;

        private ConcreteGroupTS(int tsIdx) {
            this.tsIdx = tsIdx;
        }

        @Override
        public DataType getType() {
            return DataType.TIMESTAMP;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public Object run(Object[] sample, BufferView buffer) {
            return buffer.get(0)[tsIdx];
        }

    }

}