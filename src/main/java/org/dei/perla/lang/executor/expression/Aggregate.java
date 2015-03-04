package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

import java.time.Duration;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class Aggregate implements Expression {

    protected final Expression exp;
    protected final int samples;
    protected final Duration duration;
    protected final Expression where;
    protected final DataType type;

    public Aggregate(Expression exp, int samples, Expression where) {
        this.exp = exp;
        this.samples = samples;
        duration = null;
        this.where = where;
        type = exp.getType();
    }

    public Aggregate(Expression exp, int samples, Expression where,
            DataType type) {
        this.exp = exp;
        this.samples = samples;
        duration = null;
        this.where = where;
        this.type = type;
    }

    public Aggregate(Expression exp, Duration d, Expression where) {
        this.exp = exp;
        samples = -1;
        duration = d;
        this.where = where;
        type = exp.getType();
    }

    public Aggregate(Expression exp, Duration d, Expression where,
            DataType type) {
        this.exp = exp;
        samples = -1;
        duration = d;
        this.where = where;
        this.type = type;
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public final Object run(Object[] record, BufferView view) {
        Object res;

        if (samples != -1) {
            view = view.subView(samples);
            res = doRun(view);
            view.release();
        } else if (duration != null) {
            view = view.subView(duration);
            res = doRun(view);
            view.release();
        } else {
            res = doRun(view);
        }

        return res;
    }

    protected abstract Object doRun(BufferView view);

    public static final class IntAccumulator {

        protected Integer value;

        protected IntAccumulator(Integer value) {
            this.value = value;
        }

    }

    public static final class FloatAccumulator {

        protected Float value;

        protected FloatAccumulator(Float value) {
            this.value = value;
        }

    }

}
