package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Guido Rota 13/03/15.
 */
public final class Like implements Expression {

    private final Expression e;
    private final Pattern p;

    private Like(Expression e, Pattern p) {
        this.e = e;
        this.p = p;
    }

    public static Expression create(Expression e, String pattern) {
        pattern = pattern.replace("_", ".");
        pattern = pattern.replace("%", ".*");

        return create(e, Pattern.compile(pattern));
    }

    public static Expression create(Expression e, Pattern p) {
        DataType t = e.getType();
        if (t != null && t != DataType.STRING) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "string values are allowed in operator like");
        }

        if (e instanceof Constant) {
            Object o = compute(((Constant) e).getValue(), p);
            return Constant.create(o, DataType.BOOLEAN);
        }

        return new Like(e, p);
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
        return create(e.rebuild(atts), p);
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        Object o = e.run(record, buffer);
        return compute(o, p);
    }

    private static Object compute(Object o, Pattern p) {
        if (o == null) {
            return LogicValue.UNKNOWN;
        }
        Matcher m = p.matcher((String) o);
        return LogicValue.fromBoolean(m.find());
    }

}
