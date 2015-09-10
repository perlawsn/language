package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * Expression for performing the bitwise complement of a single value.
 *
 * @author Guido Rota 27/02/15.
 */
public final class BitwiseNot extends Expression {

    private final Expression e;

    /**
     * Bitwise complement (one's complement) expression node constructor
     */
    public BitwiseNot(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return e.getType();
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        if (o == null) {
            return null;
        }
        return ~(Integer) o;
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("~(")
                .append(e)
                .append(")");
    }

}
