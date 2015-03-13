package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 12/03/15.
 */
public final class Is implements Expression {

    private final Expression e;
    private final LogicValue l;

    private Is(Expression e, LogicValue l) {
        this.e = e;
        this.l = l;
    }

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
    public Expression rebuild(List<Attribute> atts) {
        if (e.isComplete()) {
            return this;
        }
        return create(e.rebuild(atts), l);
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o = e.run(record, buffer);
        return compute(o, l);
    }

    private static Object compute(Object o, LogicValue l) {
        if (o == null) {
            return LogicValue.UNKNOWN;
        }

        return LogicValue.fromBoolean(o.equals(l));
    }

}
