package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class AvgAggregate extends Aggregate {

    private AvgAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter, DataType.FLOAT);
    }

    public static Expression create(Expression e, WindowSize ws,
            Expression filter) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: " +
                    "only float and integer expressions are allowed in " +
                    "average aggregations");
        }

        if (filter != null) {
            if (filter.getType() != null &&
                    filter.getType() != DataType.BOOLEAN) {
                return new ErrorExpression("Aggregation filter must be of " +
                        "type boolean");
            }
        }

        if (e instanceof Null || filter instanceof Null) {
            return Null.INSTANCE;
        }

        return new AvgAggregate(e, ws, filter);
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }

        Expression eNew = e.rebuild(atts);
        Expression fNew = null;
        if (filter != null) {
            fNew = filter.rebuild(atts);
        }
        return create(eNew, ws, fNew);
    }

    @Override
    public Object compute(BufferView buffer) {
        IntAccumulator count = new IntAccumulator(0);
        switch (e.getType()) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) e.run(r, b);
                    count.value++;
                }, filter);
                return si.value.floatValue() / count.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) e.run(r, b);
                    count.value++;
                }, filter);
                return sf.value / count.value;
            default:
                throw new RuntimeException(
                        "avg aggregation not defined for type " + type);
        }
    }

}
