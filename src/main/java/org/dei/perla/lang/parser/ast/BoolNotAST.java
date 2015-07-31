package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * @author Guido Rota 30/07/15.
 */
public final class BoolNotAST extends UnaryExpressionAST {

    public BoolNotAST(Token token, ExpressionAST operand) {
        super(token, operand);
    }

    @Override
    public boolean inferType(TypeVariable type, ParseContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
