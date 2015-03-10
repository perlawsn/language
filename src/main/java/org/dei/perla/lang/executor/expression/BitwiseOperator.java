package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 10/03/15.
 */
public enum BitwiseOperator {

    AND("bitwise and"),
    OR("bitwise or"),
    XOR("bitwise xor"),
    RSH("right shift"),
    LSH("left shift");

    private final String name;

    BitwiseOperator(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
