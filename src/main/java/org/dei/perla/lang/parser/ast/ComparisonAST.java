package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.ComparisonOperation;

/**
 * Comparison Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class ComparisonAST extends BinaryExpressionAST {

    private final ComparisonOperation op;

    public ComparisonAST(ComparisonOperation op, ExpressionAST left,
            ExpressionAST right) {
        super(left, right);
        this.op = op;
    }

    public ComparisonAST(Token token, ComparisonOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public ComparisonOperation getOperation() {
        return op;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.BOOLEAN);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.BOOLEAN);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        TypeVariable newBound = new TypeVariable(TypeClass.ANY);
        return left.inferType(newBound, ctx) && right.inferType(newBound, ctx);
    }

}
