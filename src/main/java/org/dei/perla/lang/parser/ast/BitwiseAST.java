package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.BitwiseOperation;


/**
 * @author Guido Rota 30/07/15.
 */
public final class BitwiseAST extends BinaryExpressionAST {

    private final BitwiseOperation op;

    public BitwiseAST(Token token, BitwiseOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public BitwiseOperation getOperation() {
        return op;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        if (!bound.restrict(TypeClass.BOOLEAN)) {
            throw new RuntimeException("add error");
        }

        return left.inferType(bound, ctx) & right.inferType(bound, ctx);
    }

}
