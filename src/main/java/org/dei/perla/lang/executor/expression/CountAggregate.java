package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

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
        super(null, ws, filter, DataType.INTEGER);
    }

    /**
     * Creates a new {@code CountAggregate} expression node.
     *
     * @param ws portion of buffer to aggregate
     * @param filter optional filtering expression to determine which samples
     *               must be aggregated
     * @return new {@code CountAggregate} instance
     */
    public static Expression create(WindowSize ws,
            Expression filter) {
        if (filter != null) {
            if (filter.getType() != null &&
                    filter.getType() != DataType.BOOLEAN) {
                return new ErrorExpression("Aggregation filter must be of " +
                        "type boolean");
            }
        }

        return new CountAggregate(ws, filter);
    }

    @Override
    public Expression bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression bf = null;
        if (filter != null) {
            bf = filter.bind(atts, bound);
        }
        return create(ws, bf);
    }

    @Override
    public Object compute(BufferView view) {
        IntAccumulator count = new IntAccumulator(0);
        view.forEach((r, b) -> count.value++, filter);
        return count.value;
    }

}
