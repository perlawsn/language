package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;

import java.time.Instant;
import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class MinAggregate extends Aggregate {

    private MinAggregate(Expression e, WindowSize ws, Expression filter) {
        super(e, ws, filter);
    }

    public static Expression create(Expression e, WindowSize ws,
            Expression filter) {
        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER &&
                t != DataType.FLOAT && t != DataType.TIMESTAMP) {
            return new ErrorExpression("Incompatible operand type: " +
                    "only float, integer and timestampjexpressions are " +
                    "allowed in min aggregations");
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

        return new MinAggregate(e, ws, filter);
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
                IntAccumulator mini = new IntAccumulator(Integer.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Integer vi = (Integer) e.run(r, b);
                    if (mini.value > vi) {
                        mini.value = vi;
                    }
                }, filter);
                return mini.value;
            case FLOAT:
                FloatAccumulator minf = new FloatAccumulator(Float.MAX_VALUE);
                buffer.forEach((r, b) -> {
                    Float vf = (Float) e.run(r, b);
                    if (minf.value > vf) {
                        minf.value = vf;
                    }
                }, filter);
                return minf.value;
            case TIMESTAMP:
                InstantAccumulator mint = new InstantAccumulator(Instant.MAX);
                buffer.forEach((r, b) -> {
                    Instant vt = (Instant) e.run(r, b);
                    if (mint.value.compareTo(vt) > 0) {
                        mint.value = vt;
                    }
                }, filter);
                return mint.value;
            default:
                throw new RuntimeException(
                        "min aggregation not defined for type " + type);
        }
    }

}
