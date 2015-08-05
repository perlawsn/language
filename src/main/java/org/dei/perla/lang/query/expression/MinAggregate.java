package org.dei.perla.lang.query.expression;

import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

import java.time.Instant;

/**
 * An {@code Expression} for determining the minimum value in a buffer. NULL
 * values are ignored, and returns NULL if all buffer values are NULL.
 *
 * @author Guido Rota 27/02/15.
 */
public final class MinAggregate extends Aggregate {

    /**
     * Minimum aggregate expression node constructor
     */
    public MinAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    @Override
    public Object compute(BufferView buffer) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case INTEGER:
                IntAccumulator mini = new IntAccumulator(null);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) e.run(r, b);
                    if (vi != null && (mini.value == null || mini.value > vi)) {
                        mini.value = vi;
                    }
                }, filter);
                return mini.value;
            case FLOAT:
                FloatAccumulator minf = new FloatAccumulator(null);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) e.run(r, b);
                    if (vf != null && (minf.value == null || minf.value > vf)) {
                        minf.value = vf;
                    }
                }, filter);
                return minf.value;
            case TIMESTAMP:
                InstantAccumulator mint = new InstantAccumulator(null);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) e.run(r, b);
                    if (vt != null && (mint.value == null ||
                            mint.value.compareTo(vt) > 0)) {
                        mint.value = vt;
                    }
                }, filter);
                return mint.value;
            default:
                throw new RuntimeException(
                        "min aggregation not defined for type " + type);
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("MIN(")
                .append(e)
                .append(", ")
                .append(ws)
                .append(", ")
                .append(filter)
                .append(")");
    }

}
