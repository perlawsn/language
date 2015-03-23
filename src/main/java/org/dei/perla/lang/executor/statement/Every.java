package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.CastInteger;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * @author Guido Rota 23/03/15.
 */
public final class Every implements Clause {

    private final Expression cond;
    private final Expression value;
    private final TemporalUnit unit;
    private boolean err;

    private Every(Expression cond, Expression value, TemporalUnit unit,
            boolean err) {
        this.cond = cond;
        this.value = value;
        this.unit = unit;
        this.err = err;
    }

    public static ClauseWrapper<Every> create(Expression cond, Expression value,
            TemporalUnit tu) {
        boolean err = false;
        String emsg = null;

        DataType t = cond.getType();
        if (t != null && t != DataType.BOOLEAN) {
            err = true;
            emsg = "Incompatible data type, if-every condition must by of " +
                    "type boolean";
        }
        t = value.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            err = true;
            emsg = "Incompatible data type, if-every sampling period must be " +
                    "a numeric value";
        }

        if (t == DataType.FLOAT) {
            value = CastInteger.create(value);
        }

        return new ClauseWrapper<>(new Every(cond, value, tu, err), emsg);
    }

    @Override
    public boolean hasErrors() {
        return value.hasErrors() || cond.hasErrors() || err;
    }

    @Override
    public boolean isComplete() {
        return cond.isComplete() && value.isComplete();
    }

    @Override
    public Every bind(List<Attribute> atts) {
        if (hasErrors()) {
            throw new IllegalStateException(
                    "Cannot bind, every clause contains errors");
        }
        if (isComplete()) {
            return this;
        }
        ClauseWrapper<Every> cw = Every.create(
                cond.bind(atts), value.bind(atts), unit);
        return cw.getClause();
    }

    public Period run(Object[] record) {
        int res = (int) value.run(record, null);
        return new Period(res, unit);
    }

}
