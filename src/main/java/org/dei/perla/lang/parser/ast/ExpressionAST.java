package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * @author Guido Rota 30/07/15.
 */
public abstract class ExpressionAST extends NodeAST {

    public ExpressionAST(Token token) {
        super(token);
    }

    public abstract boolean inferType(TypeVariable type, ParseContext ctx);

}
