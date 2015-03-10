package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 10/03/15.
 */
public enum AggregateOperation {

    SUM("sum"),
    AVG("avg"),
    MIN("min"),
    MAX("max");

    private final String name;

    AggregateOperation(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
