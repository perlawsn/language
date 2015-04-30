package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.CastInteger;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 23/03/15.
 */
public final class IfEvery {

    private final Expression cond;
    private final Expression value;
    private final TemporalUnit unit;

    private IfEvery next = null;

    private IfEvery(Expression cond, Expression value, TemporalUnit unit) {
        this.cond = cond;
        this.value = value;
        this.unit = unit;
    }

    public static IfEvery create(IfEvery previous, Expression cond,
            Expression value, TemporalUnit unit, Errors err) {
        IfEvery ife = IfEvery.create(cond, value, unit, err);
        previous.setNext(ife);
        return ife;
    }

    public static IfEvery create(Expression cond, Expression value,
            TemporalUnit unit, Errors err) {
        DataType t = cond.getType();
        if (t != null && t != DataType.BOOLEAN) {
            err.addError("Incompatible data type, IF EVERY condition must be " +
                    "of type boolean");
            return null;
        }
        t = value.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            err.addError("Incompatible data type, IF EVERY sampling period " +
                    "must be a numeric value");
            return null;
        }

        if (t == DataType.FLOAT) {
            value = CastInteger.create(value, err);
        }

        return new IfEvery(cond, value, unit);
    }

    private void setNext(IfEvery next) {
        if (this.next != null) {
            throw new IllegalStateException("next IF-EVERY node has already " +
                    "been set");
        }
        this.next = next;
    }

    public boolean isComplete() {
        if (next != null && !next.isComplete()) {
            return false;
        }
        return cond.isComplete() && value.isComplete();
    }

    public IfEvery bind(Collection<Attribute> atts,
            List<Attribute> bound, Errors err) {
        Expression bcond = cond.bind(atts, bound, err);
        Expression bvalue = value.bind(atts, bound, err);
        IfEvery ife = IfEvery.create(bcond, bvalue, unit, err);
        if (ife == null) {
            return null;
        }

        if (next != null) {
            ife.next = next.bind(atts, bound, err);
        }
        return ife;
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