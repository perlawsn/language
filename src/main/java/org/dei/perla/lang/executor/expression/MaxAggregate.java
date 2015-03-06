package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MaxAggregate extends Aggregate {

    public MaxAggregate(Expression exp, WindowSize ws, Expression where) {
        super(exp, ws, where);
    }

    @Override
    public Object doRun(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator maxi = new IntAccumulator(Integer.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) exp.run(r, b);
                    if (maxi.value < vi) {
                        maxi.value = vi;
                    }
                }, where);
                return maxi.value;
            case FLOAT:
                FloatAccumulator maxf = new FloatAccumulator(Float.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) exp.run(r, b);
                    if (maxf.value < vf) {
                        maxf.value = vf;
                    }
                }, where);
                return maxf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
