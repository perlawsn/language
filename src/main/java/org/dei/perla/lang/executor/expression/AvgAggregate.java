package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class AvgAggregate extends Aggregate {

    public final DataType opType;

    public AvgAggregate(Expression exp, WindowSize ws, Expression where) {
        super(exp, ws, where, DataType.FLOAT);
        opType = exp.getType();
    }

    @Override
    public Object doRun(BufferView buffer) {
        IntAccumulator count = new IntAccumulator(0);
        switch (opType) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) exp.run(r, b);
                    count.value++;
                }, where);
                return si.value.floatValue() / count.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) exp.run(r, b);
                    count.value++;
                }, where);
                return sf.value / count.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
