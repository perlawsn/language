package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Aggregate.IntAccumulator;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class CountAggregate implements Expression {

    private final WindowSize ws;
    private final Expression filter;

    public CountAggregate(WindowSize ws, Expression filter) {
        this.ws = ws;
        this.filter = filter;
    }

    public WindowSize getWindowSize() {
        return ws;
    }

    public Expression getFilter() {
        return filter;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        IntAccumulator count = new IntAccumulator(0);
        if (ws.getSamples() > 0) {
            buffer = buffer.subView(ws.getSamples());
            buffer.forEach((r, b) -> count.value++, filter);
            buffer.release();
        } else if (ws.getDuration() != null) {
            buffer = buffer.subView(ws.getDuration());
            buffer.forEach((r, b) -> count.value++, filter);
            buffer.release();
        } else {
            throw new RuntimeException("invalid window size");
        }

        return count.value;
    }

}
