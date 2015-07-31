package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * @author Guido Rota 30/07/15.
 */
public final class FieldAST extends ExpressionAST {

    private final String identifier;

    public FieldAST(Token token, String identifier) {
        super(token);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean inferType(TypeVariable type, ParseContext ctx) {
        return ctx.setFieldType(identifier, type);
    }

}
