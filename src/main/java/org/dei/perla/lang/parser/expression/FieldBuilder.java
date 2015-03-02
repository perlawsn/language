package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class FieldBuilder implements BuilderNode {

    private final String id;

    public FieldBuilder(String id) {
        this.id = id;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        int idx = 0;
        Attribute att = null;
        for (Attribute a : atts) {
            if (a.getId().equals(id)) {
                att = a;
                break;
            }
            idx++;
        }

        if (att == null) {
            return BuilderNode.NULL_EXPRESSION;
        }

        return new Field(idx, att.getType());
    }

}
