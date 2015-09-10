package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * An {@code Expression} for computing averages. Ignores NULL values.
 *
 * @author Guido Rota 27/02/15.
 */
public final class AvgAggregate extends Aggregate {

    /**
     * Average aggretate expression node constructor
     */
    public AvgAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter, DataType.FLOAT);
    }

    @Override
    public Object compute(BufferView buffer) {
        if (e.getType() == null) {
            return 0;
        }

        IntAccumulator count = new IntAccumulator(0);
        if (e.getType() == DataType.INTEGER) {
            IntAccumulator si = new IntAccumulator(0);
            buffer.forEach((r, b) -> {
                Integer v = (Integer) e.run(r, b);
                if (v != null) {
                    si.value += (Integer) e.run(r, b);
                    count.value++;
                }
            }, filter);
            if (count.value == 0) {
                return 0;
            } else {
                return si.value.floatValue() / count.value;
            }
        } else if (e.getType() == DataType.FLOAT) {
            FloatAccumulator sf = new FloatAccumulator(0f);
            buffer.forEach((r, b) -> {
                Float v = (Float) e.run(r, b);
                if (v != null) {
                    sf.value += (Float) e.run(r, b);
                    count.value++;
                }
            }, filter);
            if (count.value == 0) {
                return 0;
            } else {
                return sf.value / count.value;
            }
        } else {
            throw new RuntimeException(
                    "avg aggregation not defined for type " + type);
        }
    }

    @Override
    public void buildString(StringBuilder bld) {
        bld.append("AVG(")
                .append(e)
                .append(", ")
                .append(ws)
                .append(", ")
                .append(filter)
                .append(")");
    }

}
