package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class Aggregate implements Expression {

    protected final Expression exp;
    protected final WindowSize ws;
    protected final Expression where;
    protected final DataType type;

    public Aggregate(Expression exp, WindowSize ws, Expression where) {
        this.exp = exp;
        this.ws = ws;
        this.where = where;
        type = exp.getType();
    }

    public Aggregate(Expression exp, WindowSize ws, Expression where,
            DataType type) {
        this.exp = exp;
        this.ws = ws;
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

        if (ws.getSamples() > 0) {
            view = view.subView(ws.getSamples());
            res = doRun(view);
            view.release();
        } else if (ws.getDuration() != null) {
            view = view.subView(ws.getDuration());
            res = doRun(view);
            view.release();
        } else {
            throw new RuntimeException("invalid window size");
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
