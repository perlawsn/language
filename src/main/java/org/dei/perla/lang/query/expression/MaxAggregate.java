package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

import java.time.Instant;

/**
 * An {@code Expression} for determining the maximum value in a buffer. NULL
 * values are ignored, and returns NULL if all buffer values are NULL.
 *
 * @author Guido Rota 27/02/15.
 */
public final class MaxAggregate extends Aggregate {

    /**
     * Maximum aggregate expression node constructor
     */
    public MaxAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    @Override
    public Object compute(BufferView buffer) {
        if (e.equals(Constant.NULL)) {
            return null;
        }

        if (type == DataType.INTEGER) {
            IntAccumulator maxi = new IntAccumulator(null);
            buffer.forEach((r, b) -> {
                Integer vi = (Integer) e.run(r, b);
                if (vi != null && (maxi.value == null || maxi.value < vi)) {
                    maxi.value = vi;
                }
            }, filter);
            return maxi.value;
        } else if (type == DataType.FLOAT) {
            FloatAccumulator maxf = new FloatAccumulator(null);
            buffer.forEach((r, b) -> {
                Float vf = (Float) e.run(r, b);
                if (vf != null && (maxf.value == null || maxf.value < vf)) {
                    maxf.value = vf;
                }
            }, filter);
            return maxf.value;
        } else if (type == DataType.TIMESTAMP) {
            InstantAccumulator maxt = new InstantAccumulator(null);
            buffer.forEach((r, b) -> {
                Instant vt = (Instant) e.run(r, b);
                if (vt != null && (maxt.value == null ||
                        maxt.value.compareTo(vt) < 0)) {
                    maxt.value = vt;
                }
            }, filter);
            return maxt.value;
        } else {
            throw new RuntimeException(
                    "max aggregation not defined for type " + type);
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("MAX(")
                .append(e)
                .append(", ")
                .append(ws)
                .append(", ")
                .append(filter)
                .append(")");
    }

}
