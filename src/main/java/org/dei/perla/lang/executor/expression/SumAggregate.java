package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;

import java.time.Duration;

/**
 * @author Guido Rota 27/02/15.
 */
public final class SumAggregate extends Aggregate {

    public SumAggregate(Expression exp, int samples, Expression where) {
        super(exp, samples, where);
    }

    public SumAggregate(Expression exp, Duration d, Expression where) {
        super(exp, d, where);
    }

    @Override
    public Object doCompute(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) exp.compute(r, b);
                }, where);
                return si.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) exp.compute(r, b);
                }, where);
                return sf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
