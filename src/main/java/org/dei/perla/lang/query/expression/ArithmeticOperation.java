package org.dei.perla.lang.query.expression;

/**
 * Available Arithmetic operations
 *
 * @author Guido Rota 10/03/15.
 */
public enum ArithmeticOperation {

    ADDITION("+"),
    SUBTRACTION("-"),
    PRODUCT("*"),
    DIVISION("/"),
    MODULO("%");

    private final String symbol;

    ArithmeticOperation(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

}
