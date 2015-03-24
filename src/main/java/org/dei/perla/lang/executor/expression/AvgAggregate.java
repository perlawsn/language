package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.util.List;

/**
 * An {@code Expression} for computing averages.
 *
 * @author Guido Rota 27/02/15.
 */
public final class AvgAggregate extends Aggregate {

    /**
     * Private constructor, new {@code AvgAggregate} instances must be created
     * using the static {@code create} method.
     */
    private AvgAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter, DataType.FLOAT);
    }

    /**
     * Creates a new {@code AvgAggregate} expression node.
     *
     * @param e expression to average
     * @param ws portion of buffer to aggregate
     * @param filter optional filtering expression to determine which records
     *               must be aggregated
     * @return new {@code AvgAggregate} instance
     */
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

        return new AvgAggregate(e, ws, filter);
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }

        Expression eNew = e.bind(atts);
        Expression fNew = null;
        if (filter != null) {
            fNew = filter.bind(atts);
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
                    Integer v = (Integer) e.run(r, b);
                    if (v != null) {
                        si.value += (Integer) e.run(r, b);
                        count.value++;
                    }
                }, filter);
                if (count.value == 0) {
                    return 0;
                } else {
                    return si.value.floatValue() / count.value;
                }
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    Float v = (Float) e.run(r, b);
                    if (v != null) {
                        sf.value += (Float) e.run(r, b);
                        count.value++;
                    }
                }, filter);
                if (count.value == 0) {
                    return 0;
                } else {
                    return sf.value / count.value;
                }
            default:
                throw new RuntimeException(
                        "avg aggregation not defined for type " + type);
        }
    }

}
