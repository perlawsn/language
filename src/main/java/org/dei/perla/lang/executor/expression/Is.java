package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An {@code Expression} for evaluating if a boolean value corresponds to
 * a specific logic state (true, false, unknown).
 *
 * @author Guido Rota 12/03/15.
 */
public final class Is implements Expression {

    private final Expression e;
    private final LogicValue l;

    /**
     * Private constructor, new {@code Is} instances must be
     * created using the static {@code create} method.
     */
    private Is(Expression e, LogicValue l) {
        this.e = e;
        this.l = l;
    }

    /**
     * Creates a new boolean comparison expression.
     *
     * @param e operand
     * @param l logic value
     * @return an expression for evaluating logic values
     */
    public static Expression create(Expression e, LogicValue l) {
        DataType t = e.getType();
        if (t != null && t != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean values are allowed");
        }

        if (e instanceof Constant) {
            Object c = compute(((Constant) e).getValue(), l);
            return Constant.create(c, DataType.BOOLEAN);
        }

        return new Is(e, l);
    }

    public LogicValue getLogicValue() {
        return l;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public boolean hasErrors() {
        return e.hasErrors();
    }

    @Override
    public Expression bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression be = e.bind(atts, bound);
        return create(be, l);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        return compute(o, l);
    }

    private static Object compute(Object o, LogicValue l) {
        if (o == null) {
            return LogicValue.UNKNOWN;
        }

        return LogicValue.fromBoolean(o.equals(l));
    }

}
