package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public abstract class ExpressionAST extends NodeAST {

    public ExpressionAST(Token token) {
        super(token);
    }

}
