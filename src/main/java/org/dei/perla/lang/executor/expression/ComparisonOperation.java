package org.dei.perla.lang.executor.expression;

/**
 * Available comparison operations.
 *
 * @author Guido Rota 05/03/15.
 */
public enum ComparisonOperation {

    LT("<"),
    LE("<="),
    GT(">"),
    GE(">="),
    EQ("="),
    NE("!=");

    private final String symbol;

    ComparisonOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
