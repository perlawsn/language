package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

/**
 * @author Guido Rota 07/07/15.
 */
public final class SetParameter {

    private final Attribute att;
    private final Expression value;

    public SetParameter(String id, Expression value) {
        if (!(value instanceof Constant)) {
            throw new IllegalArgumentException(
                    "Set parameter expression must be constant");
        }
        DataType type = value.getType();

        this.att = Attribute.create(id, type);
        this.value = value;
    }

    public Attribute getAttribute() {
        return att;
    }

    public Expression getValue() {
        return value;
    }

}
