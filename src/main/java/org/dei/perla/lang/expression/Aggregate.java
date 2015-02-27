package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

import java.time.Duration;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * @author Guido Rota 27/02/15.
 */
public final class Aggregate implements Expression {

    private final AggregateOperation op;
    private final Expression exp;
    private final int samples;
    private final Duration duration;
    private final Expression where;

    public Aggregate(AggregateOperation op, Expression exp,
            int samples, Expression where) {
        this.op = op;
        this.exp = exp;
        this.samples = samples;
        duration = null;
        this.where = where;
    }

    public Aggregate(AggregateOperation op, Expression exp,
            Duration duration, Expression where) {
        this.op = op;
        this.exp = exp;
        samples = -1;
        this.duration = duration;
        this.where = where;
    }

    @Override
    public DataType getType() {
        return exp.getType();
    }

    @Override
    public Object compute(Object[] record, BufferView buffer) {
        if (samples != -1) {
            buffer = buffer.subView(samples);
        } else if (duration != null) {
            buffer = buffer.subView(duration);
        }

        switch (exp.getType()) {
            case INTEGER:
                IntAccumulator acc = new IntAccumulator();
                if (where == null) {
                    buffer.forEach(acc);
                } else {
                    buffer.forEach(acc, where);
                }
            case FLOAT:
            default:
                throw new RuntimeException("unexpected type " + exp.getType());
        }
    }

    private final class IntAccumulator
            implements BiConsumer<Object[], BufferView> {

        private Integer value;
        private Integer count;

        public IntAccumulator() {
            switch (op) {
                case COUNT:
                case SUM:
                case AVG:
                    value = 0;
                    break;
                case MAX:
                    value = Integer.MIN_VALUE;
                    break;
                case MIN:
                    value = Integer.MAX_VALUE;
                    break;
            }
        }

        public void accept(Object[] r, BufferView view) {
            if (op == AggregateOperation.COUNT) {
                count++;
                return;
            }

            Integer v = (Integer) exp.compute(r, view);
            switch (op) {
                case MAX:
                    if (v > value) {
                        value = v;
                    }
                    break;
                case MIN:
                    if (v < value) {
                        value = v;
                    }
                    break;
                case SUM:
                case AVG:
                    value += v;
                    break;
            }
        }

    }

    private static final class FloatAccumulator {
        public Float value;
    }

    public enum AggregateOperation {
        COUNT,
        MAX,
        MIN,
        SUM,
        AVG
    }

}
