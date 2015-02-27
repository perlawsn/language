package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Expression;
import org.dei.perla.lang.expression.Null;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public interface BuilderNode {

    public static final Expression NULL_EXPRESSION = new Null();

    public Expression build(List<Attribute> atts);

}
