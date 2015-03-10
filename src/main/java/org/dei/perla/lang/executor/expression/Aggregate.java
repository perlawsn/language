package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;
import java.util.List;

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

    public static Expression createAvg(Expression e, WindowSize ws,
            Expression filter) {
        return AvgAggregate.create(e, ws, filter);
    }

    public static Expression createMin(Expression e, WindowSize ws,
            Expression filter) {
        return MinAggregate.create(e, ws, filter);
    }

    public static Expression createMax(Expression e, WindowSize ws,
            Expression filter) {
        return MaxAggregate.create(e, ws, filter);
    }

    public static Expression createSum(Expression e, WindowSize ws,
            Expression filter) {
        return SumAggregate.create(e, ws, filter);
    }

    public static Expression createCount(WindowSize ws,
            Expression filter) {
        return CountAggregate.create(ws, filter);
    }

    @Override
    public final DataType getType() {
        return type;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete() && filter.isComplete();
    }

    @Override
    public final Object run(Object[] record, BufferView view) {
        Object res;

        if (ws.getSamples() > 0) {
            view = view.subView(ws.getSamples());
            res = compute(view);
            view.release();
        } else if (ws.getDuration() != null) {
            view = view.subView(ws.getDuration());
            res = compute(view);
            view.release();
        } else {
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

}
