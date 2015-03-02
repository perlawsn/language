package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

import java.time.Duration;

/**
 * @author Guido Rota 27/02/15.
 */
public class SumAggregate extends Aggregate {

    public SumAggregate(Expression exp, int samples, Expression where,
            DataType type) {
        super(exp, samples, where, type);
    }

    public SumAggregate(Expression exp, Duration d, Expression where,
            DataType type) {
        super(exp, d, where, type);
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        if (samples != -1) {
            buffer = buffer.subView(samples);
        } else if (duration != null) {
            buffer = buffer.subView(duration);
        }

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
