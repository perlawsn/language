package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;

import java.util.Collection;
import java.util.List;

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
     * Private constructor, new {@code Arithmetic} instances must be created
     * using the static {@code create*} methods.
     */
    private Arithmetic(ArithmeticOperation op, Expression e1, Expression e2,
            DataType type) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
        this.type = type;
    }

    /**
     * Creates an arithmetic expression that adds two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return an arithmetic expression that adds two operands.
     */
    public static Expression createAddition(Expression e1, Expression e2,
            Errors err) {
        return create(ArithmeticOperation.ADDITION, e1, e2, err);
    }

    /**
     * Creates an arithmetic expression that subtracts two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return an arithmetic expression that subtracts two operands.
     */
    public static Expression createSubtraction(Expression e1, Expression e2,
            Errors err) {
        return create(ArithmeticOperation.SUBTRACTION, e1, e2, err);
    }

    /**
     * Creates an arithmetic expression that multiplies two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return an arithmetic expression that multiplies two operands.
     */
    public static Expression createProduct(Expression e1, Expression e2,
            Errors err) {
        return create(ArithmeticOperation.PRODUCT, e1, e2, err);
    }

    /**
     * Creates an arithmetic expression that divides two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return an arithmetic expression that divides two operands.
     */
    public static Expression createDivision(Expression e1, Expression e2,
            Errors err) {
        return create(ArithmeticOperation.DIVISION, e1, e2, err);
    }

    /**
     * Creates an arithmetic expression that performs the modulo between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return an arithmetic expression that performs the modulo between two
     * operands.
     */
    public static Expression createModulo(Expression e1, Expression e2,
            Errors err) {
        return create(ArithmeticOperation.MODULO, e1, e2, err);
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

    /**
     * Creates an arithmetic expression of the desired type
     *
     * @param op operation type
     * @param e1 first operand
     * @param e2 second operand
     * @param err error tracking object
     * @return an arithmetic expression of the desired type
     */
    public static Expression create(ArithmeticOperation op,
            Expression e1, Expression e2, Errors err) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (op == ArithmeticOperation.MODULO && t1 != null && t2 != null &&
                t1 != DataType.INTEGER && t2 != DataType.INTEGER) {
            err.addError("Incompatible operand type, modulo operation is only" +
                    " allowed on integer values");
            return Constant.NULL;
        }
        if (t1 != null && t1 != DataType.INTEGER && t1 != DataType.FLOAT ||
                t2 != null && t2 != DataType.INTEGER && t2 != DataType.FLOAT) {
            err.addError("Incompatible operand type: only integer operands " +
                    "are allowed in " + op + " operations");
            return Constant.NULL;
        }

        if (t1 != t2 && t1 != null && t2 != null) {
            if (t1 == DataType.INTEGER) {
                e1 = CastFloat.create(e1, err);
                t1 = DataType.FLOAT;
            } else {
                e2 = CastFloat.create(e2, err);
                t2 = DataType.FLOAT;
            }
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            if (t1 == DataType.INTEGER) {
                return Constant.create(computeInteger(op, o1, o2), t1);
            } else {
                return Constant.create(computeFloat(op, o1, o2), t1);
            }
        }

        return new Arithmetic(op, e1, e2, t1);
    }

    public ArithmeticOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return type;
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

    private static Object computeInteger(ArithmeticOperation op,
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

    private static Object computeFloat(ArithmeticOperation op,
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


}
