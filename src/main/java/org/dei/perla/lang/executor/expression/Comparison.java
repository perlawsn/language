package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 09/03/15.
 */
public final class Comparison implements Expression {

    private final Expression e1;
    private final Expression e2;
    private final ComparisonOperation op;

    private Comparison(ComparisonOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public static Expression createLT(Expression e1, Expression e2) {
        return create(ComparisonOperation.LT, e1, e2);
    }

    public static Expression createLE(Expression e1, Expression e2) {
        return create(ComparisonOperation.LE, e1, e2);
    }

    public static Expression createGT(Expression e1, Expression e2) {
        return create(ComparisonOperation.GT, e1, e2);
    }

    public static Expression createGE(Expression e1, Expression e2) {
        return create(ComparisonOperation.GE, e1, e2);
    }

    public static Expression createEQ(Expression e1, Expression e2) {
        return create(ComparisonOperation.EQ, e1, e2);
    }

    public static Expression createNE(Expression e1, Expression e2) {
        return create(ComparisonOperation.NE, e1, e2);
    }

    public static Expression create(ComparisonOperation op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t2 != null && t1 != t2) {
            return new ErrorExpression("Incompatible operand types");
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            if (o1 == null || o2 == null) {
                return Constant.NULL_BOOLEAN;
            }
            return new Constant(compute(op, o1, o2), DataType.BOOLEAN);
        }

        return new Comparison(op, e1, e2);
    }

    public ComparisonOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean isComplete() {
        return e1.isComplete() && e2.isComplete();
    }

    @Override
    public boolean hasErrors() {
        return e1.hasErrors() || e2.hasErrors();
    }

    @Override
    public Expression rebuild(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }
        return new Comparison(op, e1.rebuild(atts), e2.rebuild(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o1 = e1.run(record, buffer);
        Object o2 = e2.run(record, buffer);

        return (compute(op, o1, o2));
    }

    private static Object compute(ComparisonOperation op, Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return null;
        }

        Comparable<Object> c1 = (Comparable<Object>) o1;
        switch (op) {
            case LT:
                return c1.compareTo(o2) < 0;
            case LE:
                return c1.compareTo(o2) <= 0;
            case GT:
                return c1.compareTo(o2) > 0;
            case GE:
                return c1.compareTo(o2) >= 0;
            case EQ:
                return c1.compareTo(o2) == 0;
            case NE:
                return c1.compareTo(o2) != 0;
            default:
                throw new RuntimeException("unknown comparison operator");
        }
    }

}
