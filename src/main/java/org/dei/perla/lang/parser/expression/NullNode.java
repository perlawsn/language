package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Null;

import java.util.List;

/**
 * @author Guido Rota 05/03/15.
 */
public class NullNode implements Node {

    @Override
    public DataType getType() {
        return null;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        return new Null();
    }

}
