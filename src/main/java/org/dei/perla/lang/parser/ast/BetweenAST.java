package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class BetweenAST extends ExpressionAST {

    private final ExpressionAST operand;
    private final ExpressionAST min;
    private final ExpressionAST max;

    public BetweenAST(Token token, ExpressionAST operand, ExpressionAST min,
            ExpressionAST max) {
        super(token);
        this.operand = operand;
        this.min = min;
        this.max = max;
    }

    private ExpressionAST getOperand() {
        return operand;
    }

    private ExpressionAST getMin() {
        return min;
    }

    private ExpressionAST getMax() {
        return max;
    }

}
