package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
import java.util.List;

/**
 * An {@code Expression} computing the complement of a boolean value
 *
 * @author Guido Rota 02/03/15.
 */
public final class Not extends Expression {

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
     * @param err error tracking object
     * @return new {@code Not} expression
     */
    public static Expression create(Expression e, Errors err) {
        if (e.getType() != null && e.getType() != DataType.BOOLEAN) {
            err.addError("Incompatible operand type: only boolean values are " +
                    "allowed in operator not");
            return Constant.NULL;
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
    public Expression doBind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression be = e.doBind(atts, bound, err);
        return create(be, err);
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        LogicValue l = (LogicValue) e.run(sample, buffer);
        return LogicValue.not(l);
    }

}
