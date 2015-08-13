package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class IfEveryAST extends NodeAST {

    private final ExpressionAST cond;
    private final EveryAST every;

    public IfEveryAST(ExpressionAST cond, EveryAST every) {
        this(null, cond, every);
    }

    public IfEveryAST(Token token, ExpressionAST cond, EveryAST every) {
        super(token);
        this.cond = cond;
        this.every = every;
    }

    public ExpressionAST getCondition() {
        return cond;
    }

    public EveryAST getEvery() {
        return every;
    }

}
