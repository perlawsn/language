package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class IsNullAST extends UnaryExpressionAST {

    public IsNullAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

}
