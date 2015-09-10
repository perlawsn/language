package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An {@code Expression} for evaluating if a boolean value corresponds to
 * a specific logic state (true, false, unknown).
 *
 * @author Guido Rota 12/03/15.
 */
public final class Is extends Expression {

    private final Expression e;
    private final LogicValue l;

    /**
     * Creates a new Is expression node
     */
    public Is(Expression e, LogicValue l) {
        this.e = e;
        this.l = l;
    }

    public LogicValue getLogicValue() {
        return l;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        return compute(o, l);
    }

    public static LogicValue compute(Object o, LogicValue l) {
        if (o == null) {
            return LogicValue.UNKNOWN;
        }

        return LogicValue.fromBoolean(o.equals(l));
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("(")
                .append(e)
                .append(" IS ")
                .append(l)
                .append(")");
    }

}
