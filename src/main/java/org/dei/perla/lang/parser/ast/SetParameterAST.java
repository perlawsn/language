package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class SetParameterAST extends NodeAST {

    private final String id;
    private final ExpressionAST value;

    public SetParameterAST(String id, ExpressionAST value) {
        this(null, id, value);
    }

    public SetParameterAST(Token token, String id,
            ExpressionAST value) {
        super(token);
        this.id = id;
        this.value = value;
    }

    public String getAttributeId() {
        return id;
    }

    public ExpressionAST getValue() {
        return value;
    }

}
