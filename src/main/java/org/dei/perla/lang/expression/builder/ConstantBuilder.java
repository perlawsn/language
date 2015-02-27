package org.dei.perla.lang.expression.builder;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Constant;
import org.dei.perla.lang.expression.Expression;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public class ConstantBuilder implements BuilderNode {

    private final Object value;
    private final DataType type;

    public ConstantBuilder(Object value, DataType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        return new Constant(value, type);
    }

}
