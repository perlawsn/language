package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.AggregateOperation;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * @author Guido Rota 30/07/15.
 */
public final class AggregateAST extends ExpressionAST {

    private final AggregateOperation op;
    private final ExpressionAST operand;
    private final WindowSize window;
    private final ExpressionAST filter;

    public AggregateAST(Token token, AggregateOperation op,
            ExpressionAST operand, WindowSize window, ExpressionAST filter) {
        super(token);
        this.op = op;
        this.operand = operand;
        this.window = window;
        this.filter = filter;
    }

    public AggregateOperation getOperation() {
        return op;
    }

    public ExpressionAST getOperand() {
        return operand;
    }

    public WindowSize getWindowSize() {
        return window;
    }

    public ExpressionAST getFilter() {
        return filter;
    }

}
