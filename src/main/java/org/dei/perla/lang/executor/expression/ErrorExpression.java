package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;
import java.util.Set;

/**
 * Placeholder {@code Expression} node returned by the various {@code
 * create} methods in case of error.
 *
 * @author Guido Rota 09/03/15.
 */
public final class ErrorExpression implements Expression {

    private final String error;

    /**
     * Creates a new {@code ErrorExpression}
     *
     * @param error error message
     */
    public ErrorExpression(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

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
    public void getFields(Set<String> fields) {
    }

    @Override
    public Expression bind(List<Attribute> atts) {
        return this;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        throw new RuntimeException("Cannot run ErrorExpression");
    }

}
