package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * A class for performing different types of comparisons between values.
 *
 * @author Guido Rota 09/03/15.
 */
public final class Comparison extends Expression {

    private final Expression e1;
    private final Expression e2;
    private final ComparisonOperation op;

    /**
     * Comparison expression node constructor
     */
    public Comparison(ComparisonOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public ComparisonOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o1 = e1.run(sample, buffer);
        Object o2 = e2.run(sample, buffer);

        return (compute(op, o1, o2));
    }

    public static Object compute(ComparisonOperation op, Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return LogicValue.UNKNOWN;
        }

        @SuppressWarnings("unchecked")
        Comparable<Object> c1 = (Comparable<Object>) o1;
        Boolean res;
        switch (op) {
            case LT:
                res = c1.compareTo(o2) < 0;
                break;
            case LE:
                res = c1.compareTo(o2) <= 0;
                break;
            case GT:
                res =  c1.compareTo(o2) > 0;
                break;
            case GE:
                res = c1.compareTo(o2) >= 0;
                break;
            case EQ:
                res = c1.compareTo(o2) == 0;
                break;
            case NE:
                res = c1.compareTo(o2) != 0;
                break;
            default:
                throw new RuntimeException("unknown comparison operator");
        }
        return LogicValue.fromBoolean(res);
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("(")
                .append(e1)
                .append(" ")
                .append(op)
                .append(" ")
                .append(e2)
                .append(")");
    }

}
