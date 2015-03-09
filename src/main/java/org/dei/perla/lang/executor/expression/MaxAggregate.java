package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MaxAggregate extends Aggregate {

    public MaxAggregate(Expression op, WindowSize ws, Expression filter) {
        super(op, ws, filter);
    }

    @Override
    public Object doRun(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator maxi = new IntAccumulator(Integer.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) op.run(r, b);
                    if (maxi.value < vi) {
                        maxi.value = vi;
                    }
                }, filter);
                return maxi.value;
            case FLOAT:
                FloatAccumulator maxf = new FloatAccumulator(Float.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) op.run(r, b);
                    if (maxf.value < vf) {
                        maxf.value = vf;
                    }
                }, filter);
                return maxf.value;
            case TIMESTAMP:
                InstantAccumulator maxt = new InstantAccumulator(Instant.MIN);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) op.run(r, b);
                    if (maxt.value.compareTo(vt) < 0) {
                        maxt.value = vt;
                    }
                }, filter);
                return maxt.value;
            default:
                throw new RuntimeException(
                        "max aggregation not defined for type " + type);
        }
    }

}
