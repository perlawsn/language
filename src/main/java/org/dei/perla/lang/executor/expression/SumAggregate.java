package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class SumAggregate extends Aggregate {

    public SumAggregate(Expression op, WindowSize ws, Expression filter) {
        super(op, ws, filter);
    }

    @Override
    public Object doRun(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) op.run(r, b);
                }, filter);
                return si.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) op.run(r, b);
                }, filter);
                return sf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
