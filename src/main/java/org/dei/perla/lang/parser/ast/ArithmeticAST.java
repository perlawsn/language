package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.ArithmeticOperation;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ArithmeticAST extends BinaryExpressionAST {

    private final ArithmeticOperation op;

    public ArithmeticAST(Token token, ArithmeticOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public ArithmeticOperation getOperation() {
        return op;
    }

}
