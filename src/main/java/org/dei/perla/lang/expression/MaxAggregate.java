package org.dei.perla.lang.expression;

import org.dei.perla.lang.executor.BufferView;

import java.time.Duration;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MaxAggregate extends Aggregate {

    public MaxAggregate(Expression exp, int samples, Expression where) {
        super(exp, samples, where);
    }

    public MaxAggregate(Expression exp, Duration d, Expression where) {
        super(exp, d, where);
    }

    @Override
    public Object doCompute(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator maxi = new IntAccumulator(Integer.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) exp.compute(r, b);
                    if (maxi.value < vi) {
                        maxi.value = vi;
                    }
                }, where);
                return maxi.value;
            case FLOAT:
                FloatAccumulator maxf = new FloatAccumulator(Float.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) exp.compute(r, b);
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
