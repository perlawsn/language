package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.fpc.Period;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.CastInteger;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.LogicValue;

import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Guido Rota 23/03/15.
 */
public final class IfEvery implements Clause {

    private final Expression cond;
    private final Expression value;
    private final TemporalUnit unit;
    private final boolean err;

    private IfEvery next = null;

    private IfEvery(Expression cond, Expression value, TemporalUnit unit,
            boolean err) {
        this.cond = cond;
        this.value = value;
        this.unit = unit;
        this.err = err;
    }

    public static ClauseWrapper<IfEvery> create(IfEvery previous,
            Expression cond, Expression value, TemporalUnit unit) {
        ClauseWrapper<IfEvery> cw = create(cond, value, unit);
        if (previous != null) {
            previous.setNext(cw.getClause());
        }
        return cw;
    }

    public static ClauseWrapper<IfEvery> create(Expression cond,
            Expression value, TemporalUnit unit) {
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

        return new ClauseWrapper<>(new IfEvery(cond, value, unit, err), emsg);
    }

    private void setNext(IfEvery next) {
        if (this.next != null) {
            throw new IllegalStateException("next IF-EVERY node has already " +
                    "been set");
        }
        this.next = next;
    }

    @Override
    public boolean hasErrors() {
        if (next != null && next.hasErrors()) {
            return true;
        }
        return value.hasErrors() || cond.hasErrors() || err;
    }

    @Override
    public boolean isComplete() {
        if (next != null && !next.isComplete()) {
            return false;
        }
        return cond.isComplete() && value.isComplete();
    }

    @Override
    public Set<String> getFields() {
        Set<String> fields = new TreeSet<>();
        fields.addAll(cond.getFields());
        fields.addAll(value.getFields());
        if (next != null) {
            fields.addAll(next.getFields());
        }
        return fields;
    }

    @Override
    public IfEvery bind(List<Attribute> atts) {
        if (hasErrors()) {
            throw new IllegalStateException(
                    "Cannot bind, every clause contains errors");
        }
        if (isComplete()) {
            return this;
        }

        ClauseWrapper<IfEvery> cw = IfEvery.create(cond.bind(atts), value.bind(atts), unit);
        IfEvery ife = cw.getClause();
        if (next != null) {
            ife.next = next.bind(atts);
        }
        return ife;
    }

    public Period run(Object[] record) {
        LogicValue c = (LogicValue) cond.run(record, null);
        if (!c.toBoolean()) {
            return next.run(record);
        }
        int v = (int) value.run(record, null);
        return new Period(v, unit);
    }

}
