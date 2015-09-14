package org.dei.perla.lang.query.statement;

import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.time.Duration;
import java.time.temporal.TemporalUnit;

/**
 * If-Every node element, used to determine the query sampling rate
 *
 * @author Guido Rota 23/03/15.
 */
public final class IfEvery {

    private final Expression cond;
    private final Expression value;
    private final TemporalUnit unit;
    private final IfEvery next;

    public IfEvery(Expression cond, Expression value, TemporalUnit unit,
            IfEvery next) {
        this.cond = cond;
        this.value = value;
        this.unit = unit;
        this.next = next;
    }

    public Expression getCondition() {
        return cond;
    }

    public Expression getValue() {
        return value;
    }

    public TemporalUnit getUnit() {
        return unit;
    }

    public IfEvery getNext() {
        return next;
    }

    public Duration run(Object[] sample) {
        LogicValue c = (LogicValue) cond.run(sample, null);
        if (!c.toBoolean()) {
            return next.run(sample);
        }
        int v = (int) value.run(sample, null);
        return Duration.of(v, unit);
    }

}
