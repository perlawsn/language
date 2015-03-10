package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class SumAggregate extends Aggregate {

    private SumAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

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

        if (e instanceof Null || filter instanceof Null) {
            return Null.INSTANCE;
        }
        if (e instanceof ErrorExpression) {
            return e;
        } else if (filter instanceof ErrorExpression) {
            return filter;
        }

        return new SumAggregate(e, ws, filter);
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
        switch (type) {
            case INTEGER:
                IntAccumulator si = new IntAccumulator(0);
                buffer.forEach((r, b) -> {
                    si.value += (Integer) e.run(r, b);
                }, filter);
                return si.value;
            case FLOAT:
                FloatAccumulator sf = new FloatAccumulator(0f);
                buffer.forEach((r, b) -> {
                    sf.value += (Float) e.run(r, b);
                }, filter);
                return sf.value;
            default:
                throw new RuntimeException(
                        "sum aggregation not defined for type " + type);
        }
    }

}
