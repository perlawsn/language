package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

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
public final class Like extends Expression {

    private final Expression e;
    private final Pattern p;

    /**
     * Like expression node constructor
     */
    public Like(Expression e, String pattern) {
        this(e, Pattern.compile(pattern));
    }

    /**
     * Like expression node constructor
     */
    public Like(Expression e, Pattern p) {
        this.e = e;
        this.p = p;
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        return compute(o, p);
    }

    public static LogicValue compute(Object o, String p) {
        return compute(o, Pattern.compile(p));
    }

    public static LogicValue compute(Object o, Pattern p) {
        if (o == null) {
            return LogicValue.UNKNOWN;
        }
        Matcher m = p.matcher((String) o);
        return LogicValue.fromBoolean(m.find());
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append("(")
                .append(e)
                .append(" LIKE ")
                .append(p)
                .append(")");
    }

}
