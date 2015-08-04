package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * {@code Bitwise} is a class representing a bitwise operation among integer
 * values.
 *
 * @author Guido Rota 10/03/15.
 */
public final class Bitwise extends Expression {

    private final BitwiseOperation op;
    private final Expression e1;
    private final Expression e2;

    /**
     * Bitwise expression node constructor
     */
    public Bitwise(BitwiseOperation op, Expression e1, Expression e2) {
        this.op = op;
        this.e1 = e1;
        this.e2 = e2;
    }

    public BitwiseOperation getOperation() {
        return op;
    }

    @Override
    public DataType getType() {
        return DataType.INTEGER;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o1 = e1.run(sample, buffer);
        Object o2 = e2.run(sample, buffer);
        return compute(op, o1, o2);
    }

    public static Object compute(BitwiseOperation op, Object o1, Object o2) {
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
