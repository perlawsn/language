package org.dei.perla.lang.parser;

import org.dei.perla.lang.executor.expression.Expression;

import java.time.temporal.TemporalUnit;

/**
 * @author Guido Rota 23/03/15.
 */
public final class Every {

    private final Expression value;
    private final TemporalUnit unit;

    public Every(Expression value, TemporalUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public Expression getValue() {
        return value;
    }

    public TemporalUnit getUnit() {
        return unit;
    }

}
