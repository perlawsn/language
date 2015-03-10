package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Field implements Expression {

    private final String id;

    public Field(String id) {
        this.id = id;
    }

    @Override
    public DataType getType() {
        return null;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        int i = 0;
        for (Attribute a : atts) {
            if (a.getId().equals(id)) {
                return new ConcreteField(i, a.getType());
            }
            i++;
        }
        return this;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return null;
    }

    private static final class ConcreteField implements Expression {

        private final DataType type;
        private final int idx;

        private ConcreteField(int idx, DataType type) {
            this.idx = idx;
            this.type = type;
        }

        @Override
        public DataType getType() {
            return type;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public Expression rebuild(List<Attribute> atts) {
            return this;
        }

        @Override
        public Object run(Object[] record, BufferView buffer) {
            return record[idx];
        }

    }

}
