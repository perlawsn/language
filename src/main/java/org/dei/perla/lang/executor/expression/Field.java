package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Field implements Expression {

    private final DataType type;
    private final int idx;

    public Field(int idx, DataType type) {
        this.idx = idx;
        this.type = type;
    }

    public static Expression create(String id) {
        return new IncompleteField(id);
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

    public int getIndex() {
        return idx;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        return record[idx];
    }

    private static final class IncompleteField implements Expression {

        private final String id;

        private IncompleteField(String id) {
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
                    return new Field(i, a.getType());
                }
                i++;
            }
            return this;
        }

        @Override
        public Object run(Object[] record, BufferView buffer) {
            return null;
        }

    }

}
