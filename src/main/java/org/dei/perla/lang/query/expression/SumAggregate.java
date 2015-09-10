package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * An {@code Expression} for determining the sum of the values in a buffer.
 * Ignores NULL values.
 *
 * @author Guido Rota 27/02/15.
 */
public final class SumAggregate extends Aggregate {

    /**
     * Maximum aggregate expression node constructor
     */
    public SumAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    @Override
    public Object compute(BufferView buffer) {
        if (type == null) {
            return 0;
        }

        if (type == DataType.INTEGER) {
            IntAccumulator si = new IntAccumulator(0);
            buffer.forEach((r, b) -> {
                Integer v = (Integer) e.run(r, b);
                if (v != null) {
                    si.value += v;
                }
            }, filter);
            return si.value;
        } else if (type == DataType.FLOAT) {
            FloatAccumulator sf = new FloatAccumulator(0f);
            buffer.forEach((r, b) -> {
                Float v = (Float) e.run(r, b);
                if (v != null) {
                    sf.value += v;
                }
            }, filter);
            return sf.value;
        } else {
            throw new RuntimeException(
                    "sum aggregation not defined for type " + type);
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("SUM(")
                .append(e)
                .append(", ")
                .append(ws)
                .append(", ")
                .append(filter)
                .append(")");
    }

}
