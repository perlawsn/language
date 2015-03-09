package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.List;

/**
 * @author Guido Rota 09/03/15.
 */
public final class ErrorNode implements Node {

    private final String msg;

    public ErrorNode(String msg) {
        this.msg = msg;
    }

    public String getError() {
        return msg;
    }

    @Override
    public DataType getType() {
        return null;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        throw new RuntimeException("Cannot build expression with error");
    }

}
