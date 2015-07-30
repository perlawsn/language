package org.dei.perla.lang.parser.ast;

/**
 * @author Guido Rota 30/07/15.
 */

import org.dei.perla.lang.parser.Token;

public abstract class BinaryExpressionAST extends ExpressionAST {

    private final ExpressionAST left;
    private final ExpressionAST right;

    public BinaryExpressionAST(Token token, ExpressionAST left,
            ExpressionAST right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    public ExpressionAST getLeftOperand() {
        return left;
    }

    public ExpressionAST getRightOperand() {
        return right;
    }

}
