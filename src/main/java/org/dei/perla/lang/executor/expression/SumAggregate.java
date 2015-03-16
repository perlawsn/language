package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.util.List;

/**
 * An {@code Expression} for determining the sum of the values in a buffer
 *
 * @author Guido Rota 27/02/15.
 */
public final class SumAggregate extends Aggregate {

    /**
     * Private constructor, new {@code SumAggregate} instances must be created
     * using the static {@code create} method.
     */
    private SumAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    /**
     * Creates a new {@code SumAggregate} expression node.
     *
     * @param e value to sum
     * @param ws portion of buffer to aggregate
     * @param filter optional filtering expression to determine which records
     *               must be aggregated
     * @return new {@code SumAggregate} instance
     */
    public static Expression create(Expression e, WindowSize ws,
            Expression filter) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: " +
                    "only float and integer expressions are allowed in " +
                    "sum aggregations");
        }

        if (filter != null) {
            if (filter.getType() != null &&
                    filter.getType() != DataType.BOOLEAN) {
                return new ErrorExpression("Aggregation filter must be of " +
                        "type boolean");
            }
        }

        return new SumAggregate(e, ws, filter);
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
        switch (type) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    Integer v = (Integer) e.run(r, b);
                    if (v != null) {
                        si.value += v;
                    }
                }, filter);
                return si.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    Float v = (Float) e.run(r, b);
                    if (v != null) {
                        sf.value += v;
                    }
                }, filter);
                return sf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
