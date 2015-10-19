package org.dei.perla.lang.query.statement;

import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * IF EVERY node element, used to determine the query sampling rate.
 *
 * @author Guido Rota 23/03/15.
 */
public final class IfEvery {

    private final Expression cond;
    private final Expression value;
    private final TemporalUnit unit;

    /**
     * Creates a new {@code IfEvery} node
     *
     * @param cond Condition
     * @param value
     * @param unit
     */
    public IfEvery(
            Expression cond,
            Expression value,
            TemporalUnit unit) {
        this.cond = cond;
        this.value = value;
        this.unit = unit;
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

    /**
     * Utility method employed to evaluate the sampling rate. The {@code
     * Duration} object returned by this method corresponds to the first
     * {@code IfEvery} condition evaluating to true.
     *
     * <p>It is the programmer's responsibility to ensure that at least one
     * of the {@code IfEvery} conditions included in the list passed as
     * parameter evaluates to true. This is usually accomplished by inserting
     * a final {@link IfEvery} object whose condition is set to {Constant.TRUE}.
     *
     * @param ifevery list of {@code IfEvery} conditions
     * @param sample data employed to evaluate the {@code IfEvery} conditions
     *               and the resulting sampling rate
     * @return sampling period
     * @throws RuntimeException when all IfEvery conditions evaluate to false
     */
    public static Duration evaluate(List<IfEvery> ifevery, Object[] sample) {
        for (IfEvery ife : ifevery) {
            Expression cond = ife.getCondition();
            LogicValue c = (LogicValue) cond.run(sample, null);
            if (c.toBoolean()) {
                int v = (int) ife.getValue().run(sample, null);
                TemporalUnit u = ife.getUnit();
                return Duration.of(v, u);
            }
        }
        throw new RuntimeException("Malformed ifevery: missing default " +
                "case (probable parser bug");
    }

}
