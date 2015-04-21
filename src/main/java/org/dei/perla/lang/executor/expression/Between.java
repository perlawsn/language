package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * A comparison expression that tests if a value lies between an inclusive
 * range.
 *
 * @author Guido Rota 12/03/15.
 */
public final class Between extends Expression {

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
     * @param err error tracking object
     * @return new {@code Between} expression
     */
    public static Expression create(Expression e, Expression min,
            Expression max, Errors err) {
        DataType t = e.getType();
        DataType tmin = min.getType();
        DataType tmax = max.getType();

        if (t != null && tmin != null && tmax != null &&
                t != tmin && t != tmax) {
            err.addError("Incompatible operand types");
            return Constant.NULL;
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
    protected Expression doBind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.doBind(atts, bound, err);
        Expression bmin = min.doBind(atts, bound, err);
        Expression bmax = max.doBind(atts, bound, err);
        return create(be, bmin, bmax, err);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        Object omin = min.run(sample, buffer);
        Object omax = max.run(sample, buffer);

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
