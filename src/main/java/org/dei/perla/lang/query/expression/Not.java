package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An {@code Expression} computing the complement of a boolean value
 *
 * @author Guido Rota 02/03/15.
 */
public final class Not extends Expression {

    private final Expression e;

    /**
     * Boolean negation expression node constructor
     */
    public Not(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        LogicValue l = (LogicValue) e.run(sample, buffer);
        return LogicValue.not(l);
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("NOT(")
                .append(e)
                .append(")");
    }

}
