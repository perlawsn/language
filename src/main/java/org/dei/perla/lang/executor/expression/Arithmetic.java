package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

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
public final class Arithmetic implements Expression {

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
     * @return an arithmetic expression that adds two operands.
     */
    public static Expression createAddition(Expression e1, Expression e2) {
        return create(ArithmeticOperation.ADDITION, e1, e2);
    }

    /**
     * Creates an arithmetic expression that subtracts two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return an arithmetic expression that subtracts two operands.
     */
    public static Expression createSubtraction(Expression e1, Expression e2) {
        return create(ArithmeticOperation.SUBTRACTION, e1, e2);
    }

    /**
     * Creates an arithmetic expression that multiplies two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return an arithmetic expression that multiplies two operands.
     */
    public static Expression createProduct(Expression e1, Expression e2) {
        return create(ArithmeticOperation.PRODUCT, e1, e2);
    }

    /**
     * Creates an arithmetic expression that divides two operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return an arithmetic expression that divides two operands.
     */
    public static Expression createDivision(Expression e1, Expression e2) {
        return create(ArithmeticOperation.DIVISION, e1, e2);
    }

    /**
     * Creates an arithmetic expression that performs the modulo between two
     * operands.
     *
     * @param e1 first operand
     * @param e2 second operand
     * @return an arithmetic expression that performs the modulo between two
     * operands.
     */
    public static Expression createModulo(Expression e1, Expression e2) {
        return create(ArithmeticOperation.MODULO, e1, e2);
    }

    /**
     * Creates an arithmetic expression that inverts the sign of its operand.
     * @param e operand
     * @return an arithmetic expression that inverts the sign of its operand.
     */
    public static Expression createInverse(Expression e) {
        return Inverse.create(e);
    }

    /**
     * Creates an arithmetic expression of the desired type
     *
     * @param op operation type
     * @param e1 first operand
     * @param e2 second operand
     * @return an arithmetic expression of the desired type
     */
    public static Expression create(ArithmeticOperation op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (op == ArithmeticOperation.MODULO && t1 != null && t2 != null &&
                t1 != DataType.INTEGER && t2 != DataType.INTEGER) {
            return new ErrorExpression("Modulo operation is only allowed on " +
                    "integer values");
        }
        if (t1 != null && t1 != DataType.INTEGER && t1 != DataType.FLOAT ||
                t2 != null && t2 != DataType.INTEGER && t2 != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer operands are allowed in " + op + " operations");
        }

        if (t1 != t2) {
            if (t1 == DataType.INTEGER) {
                e1 = CastFloat.create(e1);
                t1 = DataType.FLOAT;
            } else {
                e2 = CastFloat.create(e2);
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
    public boolean hasErrors() {
        return e1.hasErrors() || e2.hasErrors();
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }
        return create(op, e1.bind(atts), e2.bind(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o1 = e1.run(record, buffer);
        Object o2 = e2.run(record, buffer);

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
