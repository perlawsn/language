package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 09/03/15.
 */

final public class ErrorExpression implements Expression {

    private final String error;

    public ErrorExpression(String error) {
        this.error = error;
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
    public Expression rebuild(List<Attribute> atts) {
        return this;
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        throw new RuntimeException("Cannot run ErrorExpression");
    }

}
