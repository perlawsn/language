package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

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
 *     which {@link Buffer} samples are to be aggregated</li>
 * </ul>
 *
 * @author Guido Rota 27/02/15.
 */
public abstract class Aggregate extends Expression {

    protected final Expression e;
    protected final WindowSize ws;
    protected final Expression filter;
    protected final DataType type;

    protected Aggregate(Expression e, WindowSize ws, Expression filter) {
        this.e = e;
        this.ws = ws;
        this.filter = filter;
        this.type = e.getType();
    }

    protected Aggregate(Expression e, WindowSize ws, Expression filter,
            DataType type) {
        this.e = e;
        this.ws = ws;
        this.filter = filter;
        this.type = type;
    }

    public Expression getOperand() {
        return e;
    }

    public WindowSize getWindowSize() {
        return ws;
    }

    public Expression getFilter() {
        return filter;
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public final Object run(Object[] sample, BufferView view) {
        Object res;

        switch (ws.getType()) {
            case SAMPLE:
                view = view.subView(ws.getSamples());
                res = compute(view);
                view.release();
                break;
            case TIME:
                view = view.subView(ws.getDuration());
                res = compute(view);
                view.release();
                break;
            default:
                throw new RuntimeException("invalid window size");
        }

        return res;
    }

    protected abstract Object compute(BufferView view);

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

    /**
     * A simple wrapper class employed to allow the modification of final
     * closure variables in aggregation lambdas
     */
    public static final class BooleanAccumulator {

        protected boolean value;

        protected BooleanAccumulator(boolean value) {
            this.value = value;
        }

    }

}
