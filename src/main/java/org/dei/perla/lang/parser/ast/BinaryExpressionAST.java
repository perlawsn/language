package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * Abstract class representing an expression with two operands.
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class BinaryExpressionAST extends ExpressionAST {

    protected final ExpressionAST left;
    protected final ExpressionAST right;

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
