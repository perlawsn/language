package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ArithmeticInverseAST extends UnaryExpressionAST {

    public ArithmeticInverseAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

}
