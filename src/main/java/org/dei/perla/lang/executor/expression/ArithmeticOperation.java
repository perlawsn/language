package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 10/03/15.
 */
public enum ArithmeticOperation {

    SUM("+"),
    SUBTRACTION("-"),
    PRODUCT("*"),
    DIVISION("/"),
    MODULO("%");

    private final String name;

    ArithmeticOperation(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
