package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * Arithmetic inverse Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class ArithmeticInverseAST extends UnaryExpressionAST {

    public ArithmeticInverseAST(ExpressionAST operand) {
        super(operand);
    }

    public ArithmeticInverseAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.NUMERIC);
        if (!res) {
            String msg = typeErrorString("-", getPosition(),
                    bound.getTypeClass(), TypeClass.NUMERIC);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return operand.inferType(bound, ctx);
    }

}
