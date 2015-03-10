package org.dei.perla.lang.executor.expression;

/**
 * @author Guido Rota 10/03/15.
 */
public enum BitwiseOperation {

    AND("&"),
    OR("|"),
    XOR("^"),
    RSH(">>"),
    LSH("<<");

    private final String name;

    BitwiseOperation(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
