package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;

import java.util.List;

/**
 * @author Guido Rota 02/03/15.
 */
public final class Not extends UnaryExpression {

    private Not(Expression e) {
        super(e);
    }

    @Override
    public Expression create(Expression e) {
        if (e.getType() != null && e.getType() != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean values are allowed");
        }

        // Defer to later rebuild if something's missing
        if (!e.isComplete()) {
            return new Not(e);
        }

        if (e instanceof Constant) {
            Boolean b = (Boolean) ((Constant) e).getValue();
            if (b == null) {
                return Null.INSTANCE;
            }
            return new Constant(!b, DataType.BOOLEAN);
        }

        return new Not(e);
    }

    @Override
    public Object doRun(DataType type, Object o) {
        return !(Boolean) o;
    }

}
