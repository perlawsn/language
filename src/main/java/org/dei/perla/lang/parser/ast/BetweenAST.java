package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

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

    public ExpressionAST getOperand() {
        return operand;
    }

    public ExpressionAST getMin() {
        return min;
    }

    public ExpressionAST getMax() {
        return max;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
