package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MinAggregate extends Aggregate {

    public MinAggregate(Expression exp, WindowSize ws, Expression where) {
        super(exp, ws, where);
    }

    @Override
    public Object doRun(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator mini = new IntAccumulator(Integer.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) exp.run(r, b);
                    if (mini.value > vi) {
                        mini.value = vi;
                    }
                }, where);
                return mini.value;
            case FLOAT:
                FloatAccumulator minf = new FloatAccumulator(Float.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) exp.run(r, b);
                    if (minf.value > vf) {
                        minf.value = vf;
                    }
                }, where);
                return minf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
