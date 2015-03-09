package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;

/**
 * General template for the implementation of an aggregation {@link Expression}.
 *
 * <p> As for PerLa specifications, every aggregate is composed of the
 * following elements:
 * <ul>
 *     <li>an operand to be aggregated</li>
 *     <li>a {@link WindowSize} that defines the portion of {@link Buffer}
 *     on which the aggregation is to be performed</li>
 *     <li>an optional filter {@link Expression} that is used to determine
 *     which {@link Buffer} records are to be aggregated</li>
 * </ul>
 *
 * @author Guido Rota 27/02/15.
 */
public abstract class Aggregate implements Expression {

    protected final Expression op;
    protected final WindowSize ws;
    protected final Expression filter;
    protected final DataType type;

    public Aggregate(Expression op, WindowSize ws, Expression filter) {
        this.op = op;
        this.ws = ws;
        this.filter = filter;
        type = op.getType();
    }

    public Aggregate(Expression op, WindowSize ws, Expression filter,
            DataType type) {
        this.op = op;
        this.ws = ws;
        this.filter = filter;
        this.type = type;
    }

    public Expression getOperand() {
        return op;
    }

    public WindowSize getWindowSize() {
        return this.ws;
    }

    public Expression getFilter() {
        return filter;
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

    /**
     * A simple wrapper class employed to allow the modification of final
     * closure variables in aggregation lambdas
     */
    public static final class IntAccumulator {

        protected Integer value;

        protected IntAccumulator(Integer value) {
            this.value = value;
        }

    }

    /**
     * A simple wrapper class employed to allow the modification of final
     * closure variables in aggregation lambdas
     */
    public static final class FloatAccumulator {

        protected Float value;

        protected FloatAccumulator(Float value) {
            this.value = value;
        }

    }

    /**
     * A simple wrapper class employed to allow the modification of final
     * closure variables in aggregation lambdas
     */
    public static final class InstantAccumulator {

        protected Instant value;

        protected InstantAccumulator(Instant value) {
            this.value = value;
        }

    }

}
