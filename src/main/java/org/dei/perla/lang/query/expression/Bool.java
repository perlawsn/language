package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

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
     * Bool expression node constructor
     */
    public Bool(BoolOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public BoolOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o1 = e1.run(sample, buffer);
        Object o2 = e2.run(sample, buffer);
        return compute(op, o1, o2);
    }

    public static Object compute(BoolOperation op, Object o1, Object o2) {
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
