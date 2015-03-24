package org.dei.perla.lang.parser;

import org.dei.perla.lang.executor.expression.Expression;

/**
 * @author Guido Rota 16/03/15.
 */
public final class FieldSelection {

    private final Expression field;
    private final Object def;

    public FieldSelection(Expression field, Object def) {
        this.field = field;
        this.def = def;
    }

    public Expression getField() {
        return field;
    }

    public Object getDefault() {
        return def;
    }

}
