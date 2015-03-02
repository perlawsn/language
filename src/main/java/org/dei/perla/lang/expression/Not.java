package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Not extends UnaryExpression {

    public Not(Expression e) {
        super(e);
    }

    @Override
    public Object doCompute(Object o) {
        return !(Boolean) o;
    }

}
