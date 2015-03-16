package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 10/03/15.
 */
public final class Bool implements Expression {

    private final BoolOperation op;
    private final Expression e1;
    private final Expression e2;

    private Bool(BoolOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public static Expression createAND(Expression e1, Expression e2) {
        return create(BoolOperation.AND, e1, e2);
    }

    public static Expression createOR(Expression e1, Expression e2) {
        return create(BoolOperation.OR, e1, e2);
    }

    public static Expression createXOR(Expression e1, Expression e2) {
        return create(BoolOperation.XOR, e1, e2);
    }

    public static Expression createNOT(Expression e) {
        return Not.create(e);
    }

    public static Expression create(BoolOperation op,
            Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();

        if (t1 != null && t1 != DataType.BOOLEAN ||
                t2 != null && t2 != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean operands are allowed in boolean operations");
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

}