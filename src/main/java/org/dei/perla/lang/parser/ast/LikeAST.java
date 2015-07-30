package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class LikeAST extends UnaryExpressionAST {

    private final String pattern;

    public LikeAST(Token token, ExpressionAST operand, String pattern) {
        super(token, operand);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

}
