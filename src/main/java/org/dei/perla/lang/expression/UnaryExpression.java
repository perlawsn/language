package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class UnaryExpression implements Expression {

    protected final Expression e;
    protected final DataType type;

    public UnaryExpression(Expression e, DataType type) {
        this.e = e;
        this.type = type;
    }

    @Override
    public DataType getType() {
        return type;
    }

}
