package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

import java.util.Collection;
import java.util.List;

/**
 * An {@code Expression} for computing averages.
 *
 * @author Guido Rota 27/02/15.
 */
public final class AvgAggregate extends Aggregate {

    /**
     * Private constructor, new {@code AvgAggregate} instances must be created
     * using the static {@code create} method.
     */
    private AvgAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter, DataType.FLOAT);
    }

    /**
     * Creates a new {@code AvgAggregate} expression node.
     *
     * @param e expression to average
     * @param ws portion of buffer to aggregate
     * @param filter filtering expression to determine which samples must be
     *               aggregated
     * @param err error tracking object
     * @return new {@code AvgAggregate} instance
     */
    public static Expression create(Expression e, WindowSize ws,
            Expression filter, Errors err) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            err.addError("Incompatible operand type:  only float and integer " +
                    "expressions are allowed in average aggregations");
            return Constant.NULL;
        }

        if (filter.getType() != null &&
                filter.getType() != DataType.BOOLEAN) {
            err.addError("Aggregation filter must be of type boolean");
            return Constant.NULL;
        }

        return new AvgAggregate(e, ws, filter);
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.bind(atts, bound, err);
        Expression bf = filter.bind(atts, bound, err);
        return create(be, ws, bf, err);
    }

    @Override
    public Object compute(BufferView buffer) {
        if (e.getType() == null) {
            return 0;
        }

        IntAccumulator count = new IntAccumulator(0);
        switch (e.getType()) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    Integer v = (Integer) e.run(r, b);
                    if (v != null) {
                        si.value += (Integer) e.run(r, b);
                        count.value++;
                    }
                }, filter);
                if (count.value == 0) {
                    return 0;
                } else {
                    return si.value.floatValue() / count.value;
                }
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    Float v = (Float) e.run(r, b);
                    if (v != null) {
                        sf.value += (Float) e.run(r, b);
                        count.value++;
                    }
                }, filter);
                if (count.value == 0) {
                    return 0;
                } else {
                    return sf.value / count.value;
                }
            default:
                throw new RuntimeException(
                        "avg aggregation not defined for type " + type);
        }
    }

    @Override
    public void buildString(StringBuilder bld) {
        bld.append("AVG(")
                .append(e)
                .append(", ")
                .append(ws)
                .append(", ")
                .append(filter)
                .append(")");
    }

}
