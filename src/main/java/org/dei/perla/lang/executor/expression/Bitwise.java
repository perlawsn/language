package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {@code Bitwise} is a class representing a bitwise operation among integer
 * values.
 *
 * @author Guido Rota 10/03/15.
 */
public final class Bitwise implements Expression {

    private final BitwiseOperation op;
    private final Expression e1;
    private final Expression e2;

    /**
     * Private constructor, new {@code Bitwise} instances must be
     * created using the static {@code create*} methods.
     */
    private Bitwise(BitwiseOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    /**
     * Creates an expression performing a bitwise AND operation between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return a bitwise AND expression between two operands.
     */
    public static Expression createAND(Expression e1, Expression e2) {
        return create(BitwiseOperation.AND, e1, e2);
    }

    /**
     * Creates an expression performing a bitwise OR operation between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return a bitwise OR expression between two operands.
     */
    public static Expression createOR(Expression e1, Expression e2) {
        return create(BitwiseOperation.OR, e1, e2);
    }

    /**
     * Creates an expression performing a bitwise XOR operation between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return a bitwise XOR expression between two operands.
     */
    public static Expression createXOR(Expression e1, Expression e2) {
        return create(BitwiseOperation.XOR, e1, e2);
    }

    /**
     * Creates an expression performing a right shift operation
     *
     * @param e1 value to shift
     * @param e2 shift amount
     * @return a right shift expression
     */
    public static Expression createRSH(Expression e1, Expression e2) {
        return create(BitwiseOperation.RSH, e1, e2);
    }

    /**
     * Creates an expression performing a left shift operation
     *
     * @param e1 value to shift
     * @param e2 shift amount
     * @return a left shift expression
     */
    public static Expression createLSH(Expression e1, Expression e2) {
        return create(BitwiseOperation.LSH, e1, e2);
    }

    /**
     * Creates an expression that complements a value
     *
     * @param e1 value to complement
     * @return a bitwise complement expression
     */
    public static Expression createNOT(Expression e) {
        return BitwiseNot.create(e);
    }

     /**
     * Creates a bitwise expression of the desired type
     *
     * @param op operation type
     * @param e1 first operand
     * @param e2 second operand
     * @return an bitwise expression of the desired type
     */

    public static Expression create(BitwiseOperation op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.INTEGER ||
                t2 != null && t2 != DataType.INTEGER) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer operands are allowed in " + op + " comparisons");
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            return Constant.create(compute(op, o1, o2), DataType.INTEGER);
        }

        return new Bitwise(op, e1, e2);
    }

    public BitwiseOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
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
    public Expression bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression be1 = e1.bind(atts, bound);
        Expression be2 = e2.bind(atts, bound);
        return create(op, be1, be2);
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o1 = e1.run(record, buffer);
        Object o2 = e2.run(record, buffer);
        return compute(op, o1, o2);
    }

    private static Object compute(BitwiseOperation op, Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return null;
        }

        switch (op) {
            case AND:
                return (Integer) o1 & (Integer) o2;
            case OR:
                return (Integer) o1 | (Integer) o2;
            case XOR:
                return (Integer) o1 ^ (Integer) o2;
            case RSH:
                return (Integer) o1 >> (Integer) o2;
            case LSH:
                return (Integer) o1 << (Integer) o2;
            default:
                throw new RuntimeException("unknown bitwise operator " + op);
        }
    }

}
