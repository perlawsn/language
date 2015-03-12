package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 05/03/15.
 */
public enum LogicValue {

    TRUE(0, "true"),
    FALSE(1, "false"),
    UNKNOWN(2, "unknown");

    private static final LogicValue[][] AND_TABLE = new LogicValue[][] {
            { LogicValue.TRUE, LogicValue.FALSE, LogicValue.UNKNOWN },
            { LogicValue.FALSE, LogicValue.FALSE, LogicValue.FALSE },
            { LogicValue.UNKNOWN, LogicValue.FALSE, LogicValue.UNKNOWN }
    };

    private static final LogicValue[][] OR_TABLE = new LogicValue[][] {
            { LogicValue.TRUE, LogicValue.TRUE, LogicValue.TRUE },
            { LogicValue.TRUE, LogicValue.FALSE, LogicValue.UNKNOWN },
            { LogicValue.TRUE, LogicValue.UNKNOWN, LogicValue.UNKNOWN }
    };

    private static final LogicValue[][] XOR_TABLE = new LogicValue[][] {
            { LogicValue.FALSE, LogicValue.TRUE, LogicValue.UNKNOWN },
            { LogicValue.TRUE, LogicValue.FALSE, LogicValue.UNKNOWN },
            { LogicValue.UNKNOWN, LogicValue.UNKNOWN, LogicValue.UNKNOWN }
    };

    private final int idx;
    private final String name;

    LogicValue(int idx, String name) {
        this.idx = idx;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public final LogicValue not() {
        return not(this);
    }

    public static final LogicValue not(LogicValue l) {
        if (l == null || l == LogicValue.UNKNOWN) {
            return LogicValue.UNKNOWN;
        } else if (l == LogicValue.TRUE) {
            return LogicValue.FALSE;
        } else {
            return LogicValue.TRUE;
        }
    }

    public final LogicValue and(LogicValue other) {
        return and(this, other);
    }

    public static final LogicValue and(LogicValue l1, LogicValue l2) {
        return AND_TABLE[l1.idx][l2.idx];
    }

    public final LogicValue or(LogicValue other) {
        return or(this, other);
    }

    public static final LogicValue or(LogicValue l1, LogicValue l2) {
        return OR_TABLE[l1.idx][l2.idx];
    }

    public final LogicValue xor(LogicValue other) {
        return xor(this, other);
    }

    public static final LogicValue xor(LogicValue l1, LogicValue l2) {
        return XOR_TABLE[l1.idx][l2.idx];
    }

    public static final LogicValue fromBoolean(Boolean v) {
        if (v == null) {
            throw new NullPointerException();
        } else if (v) {
            return LogicValue.TRUE;
        } else {
            return LogicValue.FALSE;
        }
    }

    public static final Boolean toBoolean(LogicValue l) {
        if (l == null) {
            return false;
        }

        switch (l) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case UNKNOWN:
                return false;
            default:
                throw new RuntimeException("unknown logic value" + l);
        }
    }

}
