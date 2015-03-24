package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.*;

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

    private static final Set<String> fields;
    static {
        Set<String> fs = new TreeSet<>();
        fs.add("timestamp");
        fields = Collections.unmodifiableSet(fs);
    }

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
    public Set<String> getFields() {
        return fields;
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

        private static final List<Attribute> atts =
                Arrays.asList(new Attribute[]{Attribute.TIMESTAMP});

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
        public Set<String> getFields() {
            return fields;
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
