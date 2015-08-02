package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.ArithmeticOperation;

/**
 * An arithmetic operation with 2 operands. The inverse operation is
 * represented in the Abstract Syntax Tree using the {@link
 * ArithmeticInverseAST} class.
 *
 * @author Guido Rota 30/07/15.
 */
public final class ArithmeticAST extends BinaryExpressionAST {

    private final ArithmeticOperation op;

    public ArithmeticAST(ArithmeticOperation op, ExpressionAST left,
            ExpressionAST right) {
        super(left, right);
        this.op = op;
    }

    public ArithmeticAST(Token token, ArithmeticOperation op,
            ExpressionAST left, ExpressionAST right) {
        super(token, left, right);
        this.op = op;
    }

    public ArithmeticOperation getOperation() {
        return op;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = bound.restrict(TypeClass.NUMERIC);
        if (!res) {
            String msg = typeErrorString(op.name(), getPosition(),
                    bound.getTypeClass(), TypeClass.NUMERIC);
            ctx.addError(msg);
            return false;
        }
        setType(bound);
        return left.inferType(bound, ctx) && right.inferType(bound, ctx);
    }

}
