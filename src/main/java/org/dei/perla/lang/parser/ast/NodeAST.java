package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public abstract class NodeAST {

    private final Token token;

    public NodeAST(Token token) {
        this.token = token;
    }

}
