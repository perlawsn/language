package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * Bitwise complement Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class BitwiseNotAST extends UnaryExpressionAST {

    public BitwiseNotAST(ExpressionAST operand) {
        super(operand);
    }

    public BitwiseNotAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.INTEGER);
        if (!res) {
            String msg = typeErrorString("~", getPosition(),
                    bound.getTypeClass(), TypeClass.INTEGER);
            ctx.addError(msg);
            return false;
        }

        return operand.inferType(bound, ctx);
    }

}
