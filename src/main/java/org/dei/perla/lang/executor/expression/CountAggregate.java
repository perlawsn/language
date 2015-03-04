package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Aggregate.IntAccumulator;

import java.time.Duration;

/**
 * @author Guido Rota 27/02/15.
 */
public final class CountAggregate implements Expression {

    private final int samples;
    private final Duration duration;
    private final Expression where;

    public CountAggregate(int samples, Expression where) {
        this.samples = samples;
        duration = null;
        this.where = where;
    }

    public CountAggregate(Expression exp, Duration d, Expression where) {
        this.samples = -1;
        duration = d;
        this.where = where;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        IntAccumulator count = new IntAccumulator(0);
        if (samples != -1) {
            buffer = buffer.subView(samples);
            buffer.forEach((r, b) -> count.value++, where);
            buffer.release();
        } else if (duration != null) {
            buffer = buffer.subView(duration);
            buffer.forEach((r, b) -> count.value++, where);
            buffer.release();
        } else {
            buffer.forEach((r, b) -> count.value++, where);
        }

        return count.value;
    }

}
