package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Constant implements Expression {

    public static final Constant NULL_INTEGER =
            new Constant(null, DataType.INTEGER);
    public static final Constant NULL_FLOAT =
            new Constant(null, DataType.FLOAT);
    public static final Constant NULL_BOOLEAN =
            new Constant(null, DataType.BOOLEAN);

    public static final Constant UNKNOWN =
            new Constant(LogicValue.UNKNOWN, DataType.BOOLEAN);

    private final Object value;
    private final DataType type;

    public Constant(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
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
        return value;
    }

}
