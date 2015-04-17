package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

/**
 * An {@code Expression} for determining the maximum value in a buffer.
 *
 * @author Guido Rota 27/02/15.
 */
public final class MaxAggregate extends Aggregate {

    /**
     * Private constructor, new {@code MaxAggregate} instances must be created
     * using the static {@code create} method.
     */
    private MaxAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    /**
     * Creates a new {@code MaxAggregate} expression node.
     *
     * @param e value
     * @param ws portion of buffer to aggregate
     * @param filter optional filtering expression to determine which samples
     *               must be aggregated
     * @param err error tracking object
     * @return new {@code MaxAggregate} instance
     */
    public static Expression create(Expression e, WindowSize ws,
            Expression filter, Errors err) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER &&
                t != DataType.FLOAT && t != DataType.TIMESTAMP) {
            err.addError("Incompatible operand type: only float, integer and " +
                    "timestampjexpressions are allowed in max aggregations");
            return ErrorExpression.INSTANCE;
        }

        if (filter != null) {
            if (filter.getType() != null &&
                    filter.getType() != DataType.BOOLEAN) {
                err.addError("Aggregation filter must be of type boolean");
                return ErrorExpression.INSTANCE;
            }
        }

        return new MaxAggregate(e, ws, filter);
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.bind(atts, bound, err);
        Expression bf = null;
        if (filter != null) {
            bf = filter.bind(atts, bound, err);
        }
        return create(be, ws, bf, err);
    }

    @Override
    public Object compute(BufferView buffer) {
        if (type == null) {
            return null;
        }

        BooleanAccumulator found = new BooleanAccumulator(false);
        switch (type) {
            case INTEGER:
                IntAccumulator maxi = new IntAccumulator(Integer.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) e.run(r, b);
                    if (vi != null && maxi.value < vi) {
                        found.value = true;
                        maxi.value = vi;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return maxi.value;
                }
            case FLOAT:
                FloatAccumulator maxf = new FloatAccumulator(Float.MIN_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) e.run(r, b);
                    if (vf != null && maxf.value < vf) {
                        found.value = true;
                        maxf.value = vf;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return maxf.value;
                }
            case TIMESTAMP:
                InstantAccumulator maxt = new InstantAccumulator(Instant.MIN);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) e.run(r, b);
                    if (vt != null && maxt.value.compareTo(vt) < 0) {
                        found.value = true;
                        maxt.value = vt;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return maxt.value;
                }
            default:
                throw new RuntimeException(
                        "max aggregation not defined for type " + type);
        }
    }

}
