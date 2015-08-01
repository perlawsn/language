package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * IS NULL Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class IsNullAST extends UnaryExpressionAST {

    public IsNullAST(ExpressionAST operand) {
        super(operand);
    }

    public IsNullAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString("IS NULL", getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }

        return operand.inferType(new TypeVariable(TypeClass.ANY), ctx);
    }

}
