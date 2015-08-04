package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * {@code Arithmetic} is a class representing an arithmetic operation.
 *
 * <p>
 * Except for the modulo operation, whose operands must both be of type
 * integer, all other arithmetic operations can be performed on float and
 * integer values.
 *
 * @author Guido Rota 10/03/15.
 */
public final class Arithmetic extends Expression {

    private final ArithmeticOperation op;
    private final Expression e1;
    private final Expression e2;
    private final DataType type;

    /**
     * Arithmetic expression node constructor
     */
    public Arithmetic(ArithmeticOperation op, Expression e1, Expression e2,
            DataType type) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
        this.type = type;
    }

    /**
     * Creates an arithmetic expression that inverts the sign of its operand.
     * @param e operand
     * @param err error tracking object
     * @return an arithmetic expression that inverts the sign of its operand.
     */
    public static Expression createInverse(Expression e, Errors err) {
        return Inverse.create(e, err);
    }

    public ArithmeticOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o1 = e1.run(sample, buffer);
        Object o2 = e2.run(sample, buffer);

        if (type == null) {
            return null;
        }

        switch (type) {
            case INTEGER:
                return computeInteger(op, o1, o2);
            case FLOAT:
                return computeFloat(op, o1, o2);
            default:
                throw new RuntimeException(
                        "Unsupported arithmetic operand type");
        }
    }

    public static Object computeInteger(ArithmeticOperation op,
            Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return null;
        }

        switch(op) {
            case ADDITION:
                return (Integer) o1 + (Integer) o2;
            case SUBTRACTION:
                return (Integer) o1 - (Integer) o2;
            case PRODUCT:
                return (Integer) o1 * (Integer) o2;
            case DIVISION:
                return (Integer) o1 / (Integer) o2;
            case MODULO:
                return (Integer) o1 % (Integer) o2;
            default:
                throw new RuntimeException("unkown arithmetic operation");
        }
    }

    public static Object computeFloat(ArithmeticOperation op,
            Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return null;
        }

        switch(op) {
            case ADDITION:
                return (Float) o1 + (Float) o2;
            case SUBTRACTION:
                return (Float) o1 - (Float) o2;
            case PRODUCT:
                return (Float) o1 * (Float) o2;
            case DIVISION:
                return (Float) o1 / (Float) o2;
            case MODULO:
                throw new RuntimeException("Cannot perform modulo operation " +
                        "on float operands");
            default:
                throw new RuntimeException("unkown arithmetic operation");
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
