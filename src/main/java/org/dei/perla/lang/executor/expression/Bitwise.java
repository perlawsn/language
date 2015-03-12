package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 10/03/15.
 */
public final class Bitwise implements Expression {

    private final BitwiseOperation op;
    private final Expression e1;
    private final Expression e2;

    private Bitwise(BitwiseOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public static Expression createAND(Expression e1, Expression e2) {
        return create(BitwiseOperation.AND, e1, e2);
    }

    public static Expression createOR(Expression e1, Expression e2) {
        return create(BitwiseOperation.OR, e1, e2);
    }

    public static Expression createXOR(Expression e1, Expression e2) {
        return create(BitwiseOperation.XOR, e1, e2);
    }

    public static Expression createRSH(Expression e1, Expression e2) {
        return create(BitwiseOperation.RSH, e1, e2);
    }

    public static Expression createLSH(Expression e1, Expression e2) {
        return create(BitwiseOperation.LSH, e1, e2);
    }

    public static Expression createNOT(Expression e) {
        return BitwiseNot.create(e);
    }

    public static Expression create(BitwiseOperation op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.INTEGER ||
                t2 != null && t2 != DataType.INTEGER) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "integer operands are allowed in " + op + " comparisons");
        }

        if (e1 instanceof Null || e2 instanceof Null) {
            return Null.INSTANCE;
        }

        if (e1 instanceof Constant && e2 instanceof Constant) {
            Object o1 = ((Constant) e1).getValue();
            Object o2 = ((Constant) e2).getValue();
            if (o1 == null || o2 == null) {
                return Null.INSTANCE;
            }
            return new Constant(compute(op, o1, o2), DataType.INTEGER);
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
