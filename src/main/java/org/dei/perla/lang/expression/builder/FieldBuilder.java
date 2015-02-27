package org.dei.perla.lang.expression.builder;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Expression;
import org.dei.perla.lang.expression.Field;
import org.dei.perla.lang.expression.Null;

import java.util.List;
import java.util.Optional;

/**
 * @author Guido Rota 27/02/15.
 */
public class FieldBuilder implements BuilderNode {

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
            return new Null();
        }

        return new Field(idx, att.getType());
    }

}
