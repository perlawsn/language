package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;

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

    public Aggregate(Expression exp, int samples, Expression where,
            DataType type) {
        this.exp = exp;
        this.samples = samples;
        duration = null;
        this.where = where;
        this.type = type;
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
    public DataType getType() {
        return type;
    }

    protected final class IntAccumulator {

        protected Integer value;

        protected IntAccumulator(Integer value) {
            this.value = value;
        }

    }

    protected final class FloatAccumulator {

        protected Float value;

        protected FloatAccumulator(Float value) {
            this.value = value;
        }

    }

}
