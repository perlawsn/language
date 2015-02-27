package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;

/**
 * @author Guido Rota 27/02/15.
 */
public abstract class BinaryExpression implements Expression {

    protected final Expression e1;
    protected final Expression e2;
    protected final DataType type;

    protected BinaryExpression(Expression e1, Expression e2, DataType type) {
        this.e1 = e1;
        this.e2 = e2;
        this.type = type;
    }

    @Override
    public final DataType getType() {
        return type;
    }

}
