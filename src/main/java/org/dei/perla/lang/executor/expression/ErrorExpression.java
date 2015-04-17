package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * Placeholder {@code Expression} node returned by the various {@code
 * create} methods in case of error.
 *
 * @author Guido Rota 09/03/15.
 */
public final class ErrorExpression implements Expression {

    // Instantiating many ErrorExpression instances doesn't really make
    // sense, as each of them will be exactly the same object. Thus the
    // private constructor and the public static ErrorExpression reference.
    public static final ErrorExpression INSTANCE = new ErrorExpression();

    private ErrorExpression() { }

    @Override
    public DataType getType() {
        return null;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean hasErrors() {
        return true;
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        return this;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        throw new RuntimeException("Cannot run ErrorExpression");
    }

}
