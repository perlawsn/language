package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Aggregate.IntAccumulator;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class CountAggregate extends Aggregate {

    private CountAggregate(WindowSize ws, Expression filter) {
        super(null, ws, filter, DataType.INTEGER);
    }

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
    public Expression rebuild(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }

        Expression fNew = null;
        if (filter != null) {
            fNew = filter.rebuild(atts);
        }
        return create(ws, fNew);
    }

    @Override
    public Object compute(BufferView view) {
        IntAccumulator count = new IntAccumulator(0);
        view.forEach((r, b) -> count.value++, filter);
        return count.value;
    }

}
