package org.dei.perla.lang.executor.statement;

import java.time.temporal.TemporalUnit;

/**
 * @author Guido Rota 23/03/15.
 */
public class Period {

    private final int value;
    private final TemporalUnit unit;

    public Period(int value, TemporalUnit unit) {
        this.value = value;
        this.unit = unit;
    }

    public int getValue() {
        return value;
    }

    public TemporalUnit getUnit() {
        return unit;
    }

}
