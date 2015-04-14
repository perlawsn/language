package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.expression.CastInteger;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.LogicValue;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 23/03/15.
 */
public class IfEvery implements Clause {

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
            Expression value, TemporalUnit unit) {
        IfEvery ife = IfEvery.create(cond, value, unit);
        previous.setNext(ife);
        return ife;
    }

    public static IfEvery create(Expression cond, Expression value,
            TemporalUnit unit) {
        DataType t = cond.getType();
        if (t != null && t != DataType.BOOLEAN) {
            return new ErrorIfEvery("Incompatible data type, if-every " +
                    "condition must by of type boolean");
        }
        t = value.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            return new ErrorIfEvery("Incompatible data type, if-every " +
                    "sampling period must be a numeric value");
        }

        if (t == DataType.FLOAT) {
            value = CastInteger.create(value);
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

    @Override
    public boolean hasErrors() {
        if (next != null && next.hasErrors()) {
            return true;
        }
        return value.hasErrors() || cond.hasErrors();
    }

    @Override
    public boolean isComplete() {
        if (next != null && !next.isComplete()) {
            return false;
        }
        return cond.isComplete() && value.isComplete();
    }

    public IfEvery bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression bcond = cond.bind(atts, bound);
        Expression bvalue = value.bind(atts, bound);

        IfEvery ife = IfEvery.create(bcond, bvalue, unit);
        if (next != null) {
            ife.next = next.bind(atts, bound);
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

    /**
     * @author Guido Rota 30/03/2015
     */
    public static class ErrorIfEvery extends IfEvery {

        private final String message;

        private ErrorIfEvery(String message) {
            super(null, null, null);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean hasErrors() {
            return true;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public IfEvery bind(Collection<Attribute> atts, List<Attribute> bound) {
            return this;
        }

        public Duration run(Object[] sample) {
            throw new RuntimeException(
                    "Cannot run, IF-EVERY instance has an error");
        }

    }

}
