package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.LogicValue;

/**
 * @author Guido Rota 30/07/15.
 */
public final class IsAST extends UnaryExpressionAST {

    private final LogicValue value;

    public IsAST(Token token, ExpressionAST operand, LogicValue value) {
        super(token, operand);
        this.value = value;
    }

    public LogicValue getValue() {
        return value;
    }

    @Override
    public boolean inferType(TypeVariable type, ParseContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
