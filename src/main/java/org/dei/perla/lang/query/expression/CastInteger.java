package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An expression that performs a cast to integer. This class only supports
 * float and integer operands.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CastInteger extends Expression {

    private final Expression e;

    /**
     * Integer cast expression node
     */
    public CastInteger(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        if (o == null) {
            return null;
        }
        return ((Float) o).intValue();
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("INTEGER( ")
                .append(e)
                .append(")");
    }

}
