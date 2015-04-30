package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * A class for performing various boolean operations
 *
 * @author Guido Rota 10/03/15.
 */
public final class Bool extends Expression {

    private final BoolOperation op;
    private final Expression e1;
    private final Expression e2;

    /**
     * Private constructor, new {@code Bool} instances must be created
     * using the static {@code create*} methods.
     */
    private Bool(BoolOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Creates an expression performing a boolen AND operation between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return a boolean AND expression between two operands.
     */
    public static Expression createAND(Expression e1, Expression e2,
            Errors err) {
        return create(BoolOperation.AND, e1, e2, err);
    }

    /**
     * Creates an expression performing a boolen OR operation between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return a boolean OR expression between two operands.
     */
    public static Expression createOR(Expression e1, Expression e2,
            Errors err) {
        return create(BoolOperation.OR, e1, e2, err);
    }

    /**
     * Creates an expression performing a boolen XOR operation between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return a boolean XOR expression between two operands.
     */
    public static Expression createXOR(Expression e1, Expression e2,
            Errors err) {
        return create(BoolOperation.XOR, e1, e2, err);
    }

    /**
     * Creates an expression performing a boolean negation of a single operand
     *
     * @param e operand to negate
     * @param err error tracking object
     * @return a boolean negation
     */
    public static Expression createNOT(Expression e, Errors err) {
        return Not.create(e, err);
    }

    /**
     * Creates a booelan expression of the desired type
     *
     * @param op operation type
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return a boolean expression of the desired type
     */
    public static Expression create(BoolOperation op,
            Expression e1, Expression e2, Errors err) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.BOOLEAN ||
                t2 != null && t2 != DataType.BOOLEAN) {
            err.addError("Incompatible operand type: only boolean operands " +
                    "are allowed in boolean operations");
            return Constant.NULL;
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            return Constant.create(compute(op, o1, o2), DataType.BOOLEAN);
        }

        return new Bool(op, e1, e2);
    }

    public BoolOperation getOperation() {
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
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be1 = e1.bind(atts, bound, err);
        Expression be2 = e2.bind(atts, bound, err);
        return create(op, be1, be2, err);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o1 = e1.run(sample, buffer);
        Object o2 = e2.run(sample, buffer);
        return compute(op, o1, o2);
    }

    private static Object compute(BoolOperation op, Object o1, Object o2) {
        LogicValue l1 = (LogicValue) o1;
        LogicValue l2 = (LogicValue) o2;
        if (l1 == null) {
            l1 = LogicValue.UNKNOWN;
        }
        if (l2 == null) {
            l2 = LogicValue.UNKNOWN;
        }
        switch (op) {
            case AND:
                return l1.and(l2);
            case OR:
                return l1.or(l2);
            case XOR:
                return l1.xor(l2);
            default:
                throw new RuntimeException("unknown boolean operation " + op);
        }
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