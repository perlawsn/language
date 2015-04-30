package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

import java.util.Collection;
import java.util.List;

/**
 * An {@code Expression} for creating count aggregations.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CountAggregate extends Aggregate {

    /**
     * Private constructor, new {@code CountAggregate} instances must be
     * created using the static {@code create} method.
     */
    private CountAggregate(WindowSize ws, Expression filter) {
        super(Constant.NULL, ws, filter, DataType.INTEGER);
    }

    /**
     * Creates a new {@code CountAggregate} expression node.
     *
     * @param ws portion of buffer to aggregate
     * @param filter filtering expression to determine which samples must be
     *               aggregated
     * @param err error tracking object
     * @return new {@code CountAggregate} instance
     */
    public static Expression create(WindowSize ws,
            Expression filter, Errors err) {
        if (filter.getType() != null &&
                filter.getType() != DataType.BOOLEAN) {
            err.addError("Aggregation filter must be of type boolean");
            return Constant.NULL;
        }

        return new CountAggregate(ws, filter);
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression bf = filter.bind(atts, bound, err);
        return create(ws, bf, err);
    }

    @Override
    public Object compute(BufferView view) {
        IntAccumulator count = new IntAccumulator(0);
        view.forEach((r, b) -> count.value++, filter);
        return count.value;
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("COUNT(")
                .append(e)
                .append(", ")
                .append(ws)
                .append(", ")
                .append(filter)
                .append(")");
    }

}