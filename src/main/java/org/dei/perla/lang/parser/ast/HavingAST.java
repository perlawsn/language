package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class HavingAST extends NodeAST {

    private final ExpressionAST cond;

    public HavingAST(Token token, ExpressionAST cond) {
        super(token);
        this.cond = cond;
    }

    public ExpressionAST getCondition() {
        return cond;
    }

}
