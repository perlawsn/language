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
 * An {@code Expression} for determining the minimum value in a buffer.
 *
 * @author Guido Rota 27/02/15.
 */
public final class MinAggregate extends Aggregate {

    /**
     * Private constructor, new {@code MinAggregate} instances must be created
     * using the static {@code create} method.
     */
    private MinAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    /**
     * Creates a new {@code MinAggregate} expression node.
     *
     * @param e value
     * @param ws portion of buffer to aggregate
     * @param filter optional filtering expression to determine which samples
     *               must be aggregated
     * @param err error tracking object
     * @return new {@code MinAggregate} instance
     */
    public static Expression create(Expression e, WindowSize ws,
            Expression filter, Errors err) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER &&
                t != DataType.FLOAT && t != DataType.TIMESTAMP) {
            err.addError("Incompatible operand type: only float, integer and " +
                    "timestampjexpressions are allowed in min aggregations");
            return Constant.NULL;
        }

        if (filter != null) {
            if (filter.getType() != null &&
                    filter.getType() != DataType.BOOLEAN) {
                err.addError("Aggregation filter must be of type boolean");
                return Constant.NULL;
            }
        }

        return new MinAggregate(e, ws, filter);
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
                IntAccumulator mini = new IntAccumulator(Integer.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) e.run(r, b);
                    if (vi != null && mini.value > vi) {
                        found.value = true;
                        mini.value = vi;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return mini.value;
                }
            case FLOAT:
                FloatAccumulator minf = new FloatAccumulator(Float.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) e.run(r, b);
                    if (vf != null && minf.value > vf) {
                        found.value = true;
                        minf.value = vf;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return minf.value;
                }
            case TIMESTAMP:
                InstantAccumulator mint = new InstantAccumulator(Instant.MAX);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) e.run(r, b);
                    if (vt != null && mint.value.compareTo(vt) > 0) {
                        found.value = true;
                        mint.value = vt;
                    }
                }, filter);
                if (!found.value) {
                    return null;
                } else {
                    return mint.value;
                }
            default:
                throw new RuntimeException(
                        "min aggregation not defined for type " + type);
        }
    }

}
