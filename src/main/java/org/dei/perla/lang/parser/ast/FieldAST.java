package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class FieldAST extends ExpressionAST {

    private final String identifier;

    public FieldAST(Token token, String identifier) {
        super(token);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

}
