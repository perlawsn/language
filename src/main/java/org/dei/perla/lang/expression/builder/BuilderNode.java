package org.dei.perla.lang.expression.builder;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Expression;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public interface BuilderNode {

    public Expression build(List<Attribute> atts);

}
