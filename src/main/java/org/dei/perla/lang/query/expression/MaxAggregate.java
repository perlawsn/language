package org.dei.perla.lang.query.expression;

import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

import java.time.Instant;

/**
 * An {@code Expression} for determining the maximum value in a buffer.
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
        if (type == null) {
            return null;
        }

        BooleanAccumulator found = new BooleanAccumulator(false);
        switch (type) {
            case INTEGER:
                IntAccumulator maxi = new IntAccumulator(Integer.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) e.run(r, b);
                    if (vi != null && maxi.value < vi) {
                        found.value = true;
                        maxi.value = vi;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return maxi.value;
                }
            case FLOAT:
                FloatAccumulator maxf = new FloatAccumulator(Float.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) e.run(r, b);
                    if (vf != null && maxf.value < vf) {
                        found.value = true;
                        maxf.value = vf;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return maxf.value;
                }
            case TIMESTAMP:
                InstantAccumulator maxt = new InstantAccumulator(Instant.MIN);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) e.run(r, b);
                    if (vt != null && maxt.value.compareTo(vt) < 0) {
                        found.value = true;
                        maxt.value = vt;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return maxt.value;
                }
            default:
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
