package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * A comparison expression that tests if a value lies between an inclusive
 * range.
 *
 * @author Guido Rota 12/03/15.
 */
public final class Between extends Expression {

    private final Expression e;
    private final Expression min;
    private final Expression max;

    /**
     * Boolean expression node constructor
     */
    public Between(Expression e, Expression min, Expression max) {
        this.e = e;
        this.min = min;
        this.max = max;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        Object omin = min.run(sample, buffer);
        Object omax = max.run(sample, buffer);

        return compute(o, omin, omax);
    }

    @SuppressWarnings("unchecked")
    public static LogicValue compute(Object o, Object omin, Object omax) {
        if (o == null || omin == null || omax == null) {
            return LogicValue.UNKNOWN;
        }

        Comparable<Object> cmin = (Comparable<Object>) omin;
        Comparable<Object> cmax = (Comparable<Object>) omax;
        Boolean res = cmin.compareTo(o) <= 0 &&
                cmax.compareTo(o) >= 0;
        if (res) {
            return LogicValue.TRUE;
        } else {
            return LogicValue.FALSE;
        }
    }

    @Override
    public void buildString(StringBuilder bld) {
        bld.append("BETWEEN ")
                .append(min)
                .append(" AND ")
                .append(max);
    }

}
