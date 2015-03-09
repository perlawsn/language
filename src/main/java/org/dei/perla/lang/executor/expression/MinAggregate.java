package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MinAggregate extends Aggregate {

    public MinAggregate(Expression op, WindowSize ws, Expression filter) {
        super(op, ws, filter);
    }

    @Override
    public Object doRun(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator mini = new IntAccumulator(Integer.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) op.run(r, b);
                    if (mini.value > vi) {
                        mini.value = vi;
                    }
                }, filter);
                return mini.value;
            case FLOAT:
                FloatAccumulator minf = new FloatAccumulator(Float.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) op.run(r, b);
                    if (minf.value > vf) {
                        minf.value = vf;
                    }
                }, filter);
                return minf.value;
            case TIMESTAMP:
                InstantAccumulator mint = new InstantAccumulator(Instant.MAX);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) op.run(r, b);
                    if (mint.value.compareTo(vt) > 0) {
                        mint.value = vt;
                    }
                }, filter);
                return mint.value;
            default:
                throw new RuntimeException(
                        "min aggregation not defined for type " + type);
        }
    }

}
