package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
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

    private ExpressionAST getOperand() {
        return operand;
    }

    private ExpressionAST getMin() {
        return min;
    }

    private ExpressionAST getMax() {
        return max;
    }

    @Override
    public boolean inferType(TypeVariable type, ParseContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
