package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

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

    @Override
    public boolean inferType(TypeVariable type, ParseContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
