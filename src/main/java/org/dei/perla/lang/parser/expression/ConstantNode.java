package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class ConstantNode implements Node {

    private final Object value;
    private final DataType type;

    public ConstantNode(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public DataType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        return new Constant(value, type);
    }

}
