package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * A class for performing different types of comparisons between values.
 *
 * @author Guido Rota 09/03/15.
 */
public final class Comparison implements Expression {

    private final Expression e1;
    private final Expression e2;
    private final ComparisonOperation op;

    /**
     * Private constructor, new {@code Comparison} instances must be
     * created using the static {@code create*} methods.
     */
    private Comparison(ComparisonOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Creates a less-than comparison that returns true if the first operand
     * is smaller than the second.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new less-than comparison operation
     */
    public static Expression createLT(Expression e1, Expression e2,
            Errors err) {
        return create(ComparisonOperation.LT, e1, e2, err);
    }

    /**
     * Creates a less-than-or-equal comparison that returns true if the first
     * operand is smaller than, or equal to, the second.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new less-than-or-equal comparison operation
     */
    public static Expression createLE(Expression e1, Expression e2,
            Errors err) {
        return create(ComparisonOperation.LE, e1, e2, err);
    }

    /**
     * Creates a greater-than comparison that returns true if the first
     * operand is bigger than the second.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new greater-than comparison operation
     */
    public static Expression createGT(Expression e1, Expression e2,
            Errors err) {
        return create(ComparisonOperation.GT, e1, e2, err);
    }

    /**
     * Creates a greater-than-or-equal comparison that returns true if the first
     * operand is bigger than, or equal to, the second.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new greater-than comparison operation
     */
    public static Expression createGE(Expression e1, Expression e2,
            Errors err) {
        return create(ComparisonOperation.GE, e1, e2, err);
    }

    /**
     * Creates an equality comparison that returns true if the first
     * operand is equal to the second
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new equality comparison operation
     */
    public static Expression createEQ(Expression e1, Expression e2,
            Errors err) {
        return create(ComparisonOperation.EQ, e1, e2, err);
    }

    /**
     * Creates an inequality comparison that returns true if the first
     * operand is not equal to the second
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new inequality comparison operation
     */
    public static Expression createNE(Expression e1, Expression e2,
            Errors err) {
        return create(ComparisonOperation.NE, e1, e2, err);
    }

    /**
     * Creates a comparison expression of the desired type
     *
     * @param op operation type
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return new comparison expression of the desired type
     */
    public static Expression create(ComparisonOperation op,
            Expression e1, Expression e2, Errors err) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t2 != null && t1 != t2) {
            err.addError("Incompatible operand types");
            return ErrorExpression.INSTANCE;
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            if (o1 == null || o2 == null) {
                return Constant.UNKNOWN;
            }
            return Constant.create(compute(op, o1, o2), DataType.BOOLEAN);
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
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be1 = e1.bind(atts, bound, err);
        Expression be2 = e2.bind(atts, bound, err);
        return new Comparison(op, be1, be2);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o1 = e1.run(sample, buffer);
        Object o2 = e2.run(sample, buffer);

        return (compute(op, o1, o2));
    }

    private static Object compute(ComparisonOperation op, Object o1, Object o2) {
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

}
