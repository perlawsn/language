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

    public String getId() {
        return id;
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
    public boolean hasErrors() {
        return false;
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        int i = 0;
        for (Attribute a : atts) {
            if (!a.getId().equals(id)) {
                i++;
                continue;
            }
            if (a.getType() == DataType.BOOLEAN) {
                return new ConcreteBooleanField(i);
            } else {
                return new ConcreteField(i, a.getType());
            }
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
        public boolean hasErrors() {
            return false;
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

    /**
     * A special concrete field type deisgned to handle boolean values. This
     * field is responsible for translating the boolean data received from
     * the {@link Fpc} object into a {@link LogicValue}, which can then be
     * used in PerLa query expressions.
     */
    private static final class ConcreteBooleanField implements Expression {

        private final int idx;

        private ConcreteBooleanField(int idx) {
            this.idx = idx;
        }

        @Override
        public DataType getType() {
            return DataType.BOOLEAN;
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
        public Expression rebuild(List<Attribute> atts) {
            return this;
        }

        @Override
        public Object run(Object[] record, BufferView buffer) {
            Object o = record[idx];

            if (o == null) {
                return null;
            } else if ((Boolean) o) {
                return LogicValue.TRUE;
            } else {
                return LogicValue.FALSE;
            }
        }

    }

}
