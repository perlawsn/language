package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;
import java.util.Set;

/**
 * A comparison expression that tests if a value lies between an inclusive
 * range.
 *
 * @author Guido Rota 12/03/15.
 */
public final class Between implements Expression {

    private final Expression e;
    private final Expression min;
    private final Expression max;

    /**
     * Private constructor, new {@code Between} instances must be
     * created using the static {@code create} method.
     */
    private Between(Expression e, Expression min, Expression max) {
        this.e = e;
        this.min = min;
        this.max = max;
    }

    /**
     * Creates a new {@code Between} expression that tests if a value lies
     * between an inclusive range.
     *
     * @param e value to be tested
     * @param min minimum value allowed
     * @param max maximum value allowed
     * @return new {@code Between} expression
     */
    public static Expression create(Expression e, Expression min,
            Expression max) {
        DataType t = e.getType();
        DataType tmin = min.getType();
        DataType tmax = max.getType();

        if (t != null && tmin != null && tmax != null &&
                t != tmin && t != tmax) {
            return new ErrorExpression("Incompatible operand types");
        }

        if (e instanceof Constant && min instanceof Constant && max
                instanceof Constant) {
            Object o = ((Constant) e).getValue();
            Object omin = ((Constant) min).getValue();
            Object omax = ((Constant) max).getValue();
            return Constant.create(compute(o, omin, omax), DataType.BOOLEAN);
        }

        return new Between(e, min, max);
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete() && min.isComplete() && max.isComplete();
    }

    @Override
    public boolean hasErrors() {
        return e.hasErrors() || min.hasErrors() || max.hasErrors();
    }

    @Override
    public void getFields(Set<String> fields) {
        e.getFields(fields);
        min.getFields(fields);
        max.getFields(fields);
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }
        Expression eNew = e.bind(atts);
        Expression minNew = min.bind(atts);
        Expression maxNew = max.bind(atts);
        return create(eNew, minNew, maxNew);
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o = e.run(record, buffer);
        Object omin = e.run(record, buffer);
        Object omax = max.run(record, buffer);

        return compute(o, omin, omax);
    }

    @SuppressWarnings("unchecked")
    private static Object compute(Object o, Object omin, Object omax) {
        if (o == null || omin == null || omax == null) {
            return LogicValue.UNKNOWN;
        }

        Comparable<Object> cmin = (Comparable<Object>) omin;
        Comparable<Object> cmax = (Comparable<Object>) omax;
        Boolean res = cmin.compareTo(o) <= 0 &&
                cmax.compareTo(o) >= 0;
        if (res) {
            return LogicValue.TRUE;
        } else {
            return LogicValue.FALSE;
        }
    }

}
