package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Null;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public interface Node {

    public static final Expression NULL_EXPRESSION = new Null();

    public DataType getType();

    public Expression build(List<Attribute> atts);

}
