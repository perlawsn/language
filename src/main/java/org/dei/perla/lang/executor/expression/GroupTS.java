package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

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
public final class GroupTS implements Expression {

    @Override
    public DataType getType() {
        return DataType.TIMESTAMP;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        int i = 0;
        for (Attribute a : atts) {
            if (a == Attribute.TIMESTAMP) {
                return new ConcreteGroupTS(i);
            }
            i++;
        }
        return this;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return null;
    }

    private static final class ConcreteGroupTS implements Expression {

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
        public boolean hasErrors() {
            return false;
        }

        @Override
        public Expression bind(List<Attribute> atts) {
            return this;
        }

        @Override
        public Object run(Object[] record, BufferView buffer) {
            return buffer.get(0)[tsIdx];
        }

    }

}
