package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@code Expression} implementing a String comparison operation.
 *
 * <p>
 * The like expression supports the use of wildcards:
 * <ul>
 *     <li>_ wildcard representing a single character</li>
 *     <li>% wildcard representing zero or more characters</li>
 *     <li>[a-m] character range</li>
 * </ul>
 * </p>
 *
 * @author Guido Rota 13/03/15.
 */
public final class Like implements Expression {

    private final Expression e;
    private final Pattern p;

    /**
     * Private constructor, new {@code Like} instances must be
     * created using the static {@code create} method.
     */
    private Like(Expression e, Pattern p) {
        this.e = e;
        this.p = p;
    }

    /**
     * Creates a new expression that indicates if a string operand matches
     * the given pattern.
     *
     * @param e operand
     * @param pattern pattern to be matched
     * @return new {@code Like} expression
     */
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
    public Expression bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression be = e.bind(atts, bound);
        return create(be, p);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
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
