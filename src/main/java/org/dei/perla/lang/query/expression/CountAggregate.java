package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * An {@code Expression} for creating count aggregations.
 *
 * @author Guido Rota 27/02/15.
 */
public final class CountAggregate extends Aggregate {

    /**
     * Maximum aggregate expression node constructor
     */
    public CountAggregate(WindowSize ws, Expression filter) {
        super(Constant.NULL, ws, filter, DataType.INTEGER);
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
