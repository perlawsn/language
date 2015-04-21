package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An {@link Expression} for accessing the value of a specific Fpc
 * attribute sample.
 *
 * <p>
 * Newly created {@code Field} objects must be bound to an actual attribute
 * list before being used.
 *
 * @author Guido Rota 23/02/15.
 */
public class Field extends Expression {

    private final String id;

    /**
     * Creates a new Field for accessing the value of a data attribute
     * generated by an Fpc.
     *
     * @param id attribute identifier
     */
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
    public Expression doBind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        int i = Expression.indexOf(id, bound);
        if (i != -1) {
            return create(i, bound.get(i));
        }

        Attribute a = Expression.getById(id, atts);
        if (a == null) {
            return this;
        }

        bound.add(a);
        return create(bound.size() - 1, a);
    }

    private Expression create(int idx, Attribute att) {
        if (att.getType() == DataType.BOOLEAN) {
            return new ConcreteBooleanField(idx, att);
        } else {
            return new ConcreteField(idx, att);
        }
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        return null;
    }

    private static class ConcreteField extends Field {

        protected final int idx;
        private final DataType type;

        private ConcreteField(int idx, Attribute att) {
            super(att.getId());
            this.idx = idx;
            this.type = att.getType();
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
        public Object run(Object[] sample, BufferView buffer) {
            return sample[idx];
        }

    }

    /**
     * A special concrete field type deisgned to handle boolean values. This
     * field is responsible for translating the boolean data received from
     * the {@link Fpc} object into a {@link LogicValue}, which can then be
     * used in PerLa query expressions.
     */
    private static final class ConcreteBooleanField extends ConcreteField {

        private ConcreteBooleanField(int idx, Attribute att) {
            super(idx, att);
        }

        @Override
        public Object run(Object[] sample, BufferView buffer) {
            Object o = sample[idx];

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
