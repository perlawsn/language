package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Constant implements Expression {

    // CONSTANT NULL VALUES
    public static final Constant NULL_INTEGER =
            new Constant(null, DataType.INTEGER);
    public static final Constant NULL_FLOAT =
            new Constant(null, DataType.FLOAT);
    public static final Constant NULL_STRING =
            new Constant(null, DataType.STRING);
    public static final Constant NULL_BOOLEAN =
            new Constant(null, DataType.BOOLEAN);
    public static final Constant NULL_TIMESTAMP =
            new Constant(null, DataType.TIMESTAMP);
    public static final Constant NULL_ID =
            new Constant(null, DataType.ID);

    // CONSTANT LOGIC VALUES
    public static final Constant TRUE =
            new Constant(LogicValue.TRUE, DataType.BOOLEAN);
    public static final Constant FALSE =
            new Constant(LogicValue.FALSE, DataType.BOOLEAN);
    public static final Constant UNKNOWN =
            new Constant(LogicValue.UNKNOWN, DataType.BOOLEAN);

    // CONSTANT INTEGER VALUES (MOST COMMONLY USED)
    public static final Constant INTEGER_0 =
            new Constant(0, DataType.INTEGER);
    public static final Constant INTEGER_1 =
            new Constant(1, DataType.INTEGER);
    public static final Constant INTEGER_2 =
            new Constant(2, DataType.INTEGER);

    private final Object value;
    private final DataType type;

    private Constant(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    public static Expression create(Object value, DataType type) {
        if (value == null) {
            switch (type) {
                case INTEGER:
                    return NULL_INTEGER;
                case FLOAT:
                    return NULL_FLOAT;
                case STRING:
                    return NULL_STRING;
                case BOOLEAN:
                    return NULL_BOOLEAN;
                case TIMESTAMP:
                    return NULL_TIMESTAMP;
                case ID:
                    return NULL_ID;
                default:
                    throw new RuntimeException("unknown data type " + type);
            }
        }

        if (type == DataType.BOOLEAN) {
            switch ((LogicValue) value) {
                case TRUE:
                    return TRUE;
                case FALSE:
                    return FALSE;
                case UNKNOWN:
                    return UNKNOWN;
                default:
                    throw new RuntimeException("unknown logic value " + value);
            }
        }

        if (type == DataType.INTEGER) {
            switch ((Integer) value) {
                case 0:
                    return INTEGER_0;
                case 1:
                    return INTEGER_1;
                case 2:
                    return INTEGER_2;
            }
        }

        return new Constant(value, type);
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
