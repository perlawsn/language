package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 10/03/15.
 */
public final class Arithmetic implements Expression {

    private final ArithmeticOperation op;
    private final Expression e1;
    private final Expression e2;
    private final DataType type;

    private Arithmetic(ArithmeticOperation op, Expression e1, Expression e2,
            DataType type) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
        this.type = type;
    }

    public static Expression createSum(Expression e1, Expression e2) {
        return create(ArithmeticOperation.SUM, e1, e2);
    }

    public static Expression createSubtraction(Expression e1, Expression e2) {
        return create(ArithmeticOperation.SUBTRACTION, e1, e2);
    }

    public static Expression createProduct(Expression e1, Expression e2) {
        return create(ArithmeticOperation.PRODUCT, e1, e2);
    }

    public static Expression createDivision(Expression e1, Expression e2) {
        return create(ArithmeticOperation.DIVISION, e1, e2);
    }

    public static Expression createModulo(Expression e1, Expression e2) {
        return create(ArithmeticOperation.MODULO, e1, e2);
    }

    public static Expression createInverse(Expression e) {
        return Inverse.create(e);
    }

    public static Expression create(ArithmeticOperation op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (op == ArithmeticOperation.MODULO &&
                t1 != DataType.INTEGER && t2 != DataType.INTEGER) {
            return new ErrorExpression("Modulo operation is only allowed on " +
                    "integer values");
        }
        if (t1 != null && t1 != DataType.INTEGER && t1 != DataType.FLOAT ||
                t2 != null && t2 != DataType.INTEGER && t2 != DataType.FLOAT) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer operands are allowed in " + op + " operations");
        }

        if (e1 instanceof Null || e2 instanceof Null) {
            return Null.INSTANCE;
        }
        if (e1 instanceof ErrorExpression) {
            return e1;
        } else if (e2 instanceof ErrorExpression) {
            return e2;
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
            if (o1 == null || o2 == null) {
                return Null.INSTANCE;
            }
            if (t1 == DataType.INTEGER) {
                return new Constant(computeInteger(op, o1, o2), t1);
            } else {
                return new Constant(computeFloat(op, o1, o2), t1);
            }
        }

        return new Arithmetic(op, e1, e2, t1);
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
    public Expression rebuild(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }
        return create(op, e1.rebuild(atts), e2.rebuild(atts));
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o1 = e1.run(record, buffer);
        Object o2 = e2.run(record, buffer);

        switch (type) {
            case INTEGER:
                computeInteger(op, o1, o2);
            case FLOAT:
                computeFloat(op, o1, o2);
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
            case SUM:
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
            case SUM:
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
