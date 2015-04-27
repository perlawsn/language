package org.dei.perla.lang.executor.expression;

/**
 * Available Bitwise operations
 *
 * @author Guido Rota 10/03/15.
 */
public enum BitwiseOperation {

    AND("&"),
    OR("|"),
    XOR("^"),
    RSH(">>"),
    LSH("<<");

    private final String symbol;

    BitwiseOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
