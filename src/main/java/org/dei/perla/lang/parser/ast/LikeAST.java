package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * LIKE Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class LikeAST extends UnaryExpressionAST {

    private final String pattern;

    public LikeAST(ExpressionAST operand, String pattern) {
        super(operand);
        this.pattern = pattern;
    }

    public LikeAST(Token token, ExpressionAST operand, String pattern) {
        super(token, operand);
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("LIKE", getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        TypeVariable newBound = new TypeVariable(TypeClass.STRING);
        return operand.inferType(newBound, ctx);
    }

}
