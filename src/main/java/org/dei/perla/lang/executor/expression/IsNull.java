package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An expression for evaluating if a value is null.
 *
 * @author Guido Rota 13/03/15.
 */
public final class IsNull implements Expression {

    private final Expression e;

    /**
     * Private constructor, new {@code Inverse} instances must be
     * created using the static {@code create} method.
     */
    private IsNull(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new expression that evaluates if its operand is null
     *
     * @param e operand
     * @return a new expression that evaluates if its operand is null.
     */
    public static Expression create(Expression e) {
        if (e instanceof Constant) {
            if (((Constant) e).getValue() == null) {
                return Constant.TRUE;
            } else {
                return Constant.FALSE;
            }
        }
        return new IsNull(e);
    }

    @Override
    public DataType getType() {
        return DataType.BOOLEAN;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public boolean hasErrors() {
        return e.hasErrors();
    }

    @Override
    public Expression bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.bind(atts, bound, err);
        return create(be);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = e.run(sample, buffer);
        if (o == null) {
            return LogicValue.TRUE;
        } else {
            return LogicValue.FALSE;
        }
    }

}
