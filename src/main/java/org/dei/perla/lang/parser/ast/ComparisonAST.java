package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.ComparisonOperation;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ComparisonAST extends BinaryExpressionAST {

    private final ComparisonOperation op;

    public ComparisonAST(Token token, ComparisonOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public ComparisonOperation getOperation() {
        return op;
    }

}
