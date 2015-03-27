package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An {@code Expression} computing the complement of a boolean value
 *
 * @author Guido Rota 02/03/15.
 */
public final class Not implements Expression {

    private final Expression e;

    /**
     * Private constructor, new {@code Not} instances must be created
     * using the static {@code create} method.
     */
    private Not(Expression e) {
        this.e = e;
    }

    /**
     * Creates a new expression that inverts the boolean value of its operand
     *
     * @param e operand
     * @return new {@code Not} expression
     */
    public static Expression create(Expression e) {
        if (e.getType() != null && e.getType() != DataType.BOOLEAN) {
            return new ErrorExpression("Incompatible operand type: only " +
                    "boolean values are allowed in operator not");
        }

        if (e instanceof Constant) {
            LogicValue l = (LogicValue) ((Constant) e).getValue();
            return Constant.create(LogicValue.not(l), DataType.BOOLEAN);
        }

        return new Not(e);
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
    public Expression bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression be = e.bind(atts, bound);
        return create(be);
    }

    @Override
    public Object run(Object[] record, BufferView buffer) {
        LogicValue l = (LogicValue) e.run(record, buffer);
        return LogicValue.not(l);
    }

}
