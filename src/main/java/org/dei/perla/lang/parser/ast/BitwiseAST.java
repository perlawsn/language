package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.BitwiseOperation;


/**
 * A bitwise operation with 2 operands. The bitwise complement operation is
 * represented in the Abstract Syntax Tree using the {@link BitwiseNotAST} class.
 *
 * @author Guido Rota 30/07/15.
 */
public final class BitwiseAST extends BinaryExpressionAST {

    private final BitwiseOperation op;

    public BitwiseAST(BitwiseOperation op, ExpressionAST left,
            ExpressionAST right) {
        super(left, right);
        this.op = op;
    }

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
        boolean res = bound.restrict(TypeClass.INTEGER);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.INTEGER);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return left.inferType(bound, ctx) && right.inferType(bound, ctx);
    }

}
