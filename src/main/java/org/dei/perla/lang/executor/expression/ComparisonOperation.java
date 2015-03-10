package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 05/03/15.
 */
public enum ComparisonOperation {

    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    EQ("="),
    NE("!=");

    private final String name;

    ComparisonOperation(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
