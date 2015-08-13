package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class FieldSelectionAST extends NodeAST {

    private final ExpressionAST field;
    private final ExpressionAST defValue;

    public FieldSelectionAST(ExpressionAST field,
            ExpressionAST defValue) {
        this(null, field, defValue);
    }

    public FieldSelectionAST(Token token, ExpressionAST field,
            ExpressionAST defValue) {
        super(token);
        this.field = field;
        this.defValue = defValue;
    }

    public ExpressionAST getField() {
        return field;
    }

    public ExpressionAST getDefault() {
        return defValue;
    }

}
