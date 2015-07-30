package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.BoolOperation;

/**
 * @author Guido Rota 30/07/15.
 */
public final class BoolAST extends BinaryExpressionAST {

    private final BoolOperation op;

    public BoolAST(Token token, BoolOperation op, ExpressionAST left,
            ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public BoolOperation getOperation() {
        return op;
    }

}
