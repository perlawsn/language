package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * A class representing an expression returning a constant value
 *
 * @author Guido Rota 23/02/15.
 */
public final class Constant extends Expression {

    // CONSTANT NULL
    public static final Constant NULL =
            new Constant(null, DataType.ANY);

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

    public static Constant create(Object value, DataType type) {
        if (value == null) {
            return Constant.NULL;
        }

        if (type == DataType.BOOLEAN) {
            switch ((LogicValue) value) {
                case TRUE:
                    return Constant.TRUE;
                case FALSE:
                    return Constant.FALSE;
                case UNKNOWN:
                    return Constant.UNKNOWN;
                default:
                    throw new RuntimeException("unknown logic value " + value);
            }
        }

        if (type == DataType.INTEGER) {
            switch ((Integer) value) {
                case 0:
                    return Constant.INTEGER_0;
                case 1:
                    return Constant.INTEGER_1;
                case 2:
                    return Constant.INTEGER_2;
            }
        }

        return new Constant(value, type);
    }

    /**
     * Private Constant constructor. To create new Constant objects, use the
     * static create method instead.
     */
    private Constant(Object value, DataType type) {
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
    public Object run(Object[] sample, BufferView buffer) {
        return value;
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append(value);
    }

}
