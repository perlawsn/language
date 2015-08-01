package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public abstract class UnaryExpressionAST extends ExpressionAST {

    protected final ExpressionAST operand;

    public UnaryExpressionAST(ExpressionAST operand) {
        this.operand = operand;
    }

    public UnaryExpressionAST(Token token, ExpressionAST operand) {
        super(token);
        this.operand = operand;
    }

    public ExpressionAST getOperand() {
        return operand;
    }

}
