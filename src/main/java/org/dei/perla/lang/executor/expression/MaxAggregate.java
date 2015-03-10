package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;
import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MaxAggregate extends Aggregate {

    private MaxAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    public static Expression create(Expression e, WindowSize ws,
            Expression filter) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER &&
                t != DataType.FLOAT && t != DataType.TIMESTAMP) {
            return new ErrorExpression("Incompatible operand type: " +
                    "only float, integer and timestampjexpressions are " +
                    "allowed in max aggregations");
        }

        if (filter != null) {
            if (filter.getType() != null &&
                    filter.getType() != DataType.BOOLEAN) {
                return new ErrorExpression("Aggregation filter must be of " +
                        "type boolean");
            }
        }

        if (e instanceof Null || filter instanceof Null) {
            return Null.INSTANCE;
        }
        if (e instanceof ErrorExpression) {
            return e;
        } else if (filter instanceof ErrorExpression) {
            return filter;
        }

        return new MaxAggregate(e, ws, filter);
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }

        Expression eNew = e.rebuild(atts);
        Expression fNew = null;
        if (filter != null) {
            fNew = filter.rebuild(atts);
        }
        return create(eNew, ws, fNew);
    }

    @Override
    public Object compute(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator maxi = new IntAccumulator(Integer.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) e.run(r, b);
                    if (maxi.value < vi) {
                        maxi.value = vi;
                    }
                }, filter);
                return maxi.value;
            case FLOAT:
                FloatAccumulator maxf = new FloatAccumulator(Float.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) e.run(r, b);
                    if (maxf.value < vf) {
                        maxf.value = vf;
                    }
                }, filter);
                return maxf.value;
            case TIMESTAMP:
                InstantAccumulator maxt = new InstantAccumulator(Instant.MIN);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) e.run(r, b);
                    if (maxt.value.compareTo(vt) < 0) {
                        maxt.value = vt;
                    }
                }, filter);
                return maxt.value;
            default:
                throw new RuntimeException(
                        "max aggregation not defined for type " + type);
        }
    }

}
