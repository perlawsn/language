package org.dei.perla.lang.executor.expression;

import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

/**
 * @author Guido Rota 27/02/15.
 */
public final class SumAggregate extends Aggregate {

    public SumAggregate(Expression exp, WindowSize ws, Expression where) {
        super(exp, ws, where);
    }

    @Override
    public Object doRun(BufferView buffer) {
        switch (type) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) exp.run(r, b);
                }, where);
                return si.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) exp.run(r, b);
                }, where);
                return sf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
