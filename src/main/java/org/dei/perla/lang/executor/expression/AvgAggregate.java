package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class AvgAggregate extends Aggregate {

    public final DataType opType;

    public AvgAggregate(Expression op, WindowSize ws, Expression filter) {
        super(op, ws, filter, DataType.FLOAT);
        opType = op.getType();
    }

    @Override
    public Object doRun(BufferView buffer) {
        IntAccumulator count = new IntAccumulator(0);
        switch (opType) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) op.run(r, b);
                    count.value++;
                }, filter);
                return si.value.floatValue() / count.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) op.run(r, b);
                    count.value++;
                }, filter);
                return sf.value / count.value;
            default:
                throw new RuntimeException(
                        "avg aggregation not defined for type " + type);
        }
    }

}
