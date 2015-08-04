package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An expression that performs a cast to float. This class only supports
 * float and integer operands.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CastFloat extends Expression {

    private final Expression e;

    /**
     * Float cast expression node
     */
    public CastFloat(Expression e) {
        this.e = e;
    }

    @Override
    public DataType getType() {
        return DataType.FLOAT;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        if (o == null) {
            return null;
        }

        return ((Integer) o).floatValue();
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("FLOAT( ")
                .append(e)
                .append(")");
    }

}
