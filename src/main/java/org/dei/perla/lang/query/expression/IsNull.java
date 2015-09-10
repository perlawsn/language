package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An expression for evaluating if a value is null.
 *
 * @author Guido Rota 13/03/15.
 */
public final class IsNull extends Expression {

    private final Expression e;

    /**
     * Creates a new IsNull expression node
     */
    public IsNull(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new expression that evaluates if its operand is null
     *
     * @param e operand
     * @return a new expression that evaluates if its operand is null.
     */
    public static Expression create(Expression e) {
        if (e instanceof Constant) {
            if (((Constant) e).getValue() == null) {
                return Constant.TRUE;
            } else {
                return Constant.FALSE;
            }
        }
        return new IsNull(e);
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        return compute(o);
    }

    public static LogicValue compute(Object o) {
        if (o == null) {
            return LogicValue.TRUE;
        } else {
            return LogicValue.FALSE;
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("(")
                .append(e)
                .append(" IS NULL)");
    }

}
