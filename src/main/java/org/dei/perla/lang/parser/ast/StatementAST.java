package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public abstract class StatementAST extends NodeAST {

    public StatementAST(Token token) {
        super(token);
    }

}
