package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.FieldSelection;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

/**
 * @author Guido Rota 30/07/15.
 */
public final class FieldSelectionAST extends NodeAST {

    private final ExpressionAST field;
    private final ExpressionAST def;

    public FieldSelectionAST(ExpressionAST field,
            ExpressionAST def) {
        this(null, field, def);
    }

    public FieldSelectionAST(Token token, ExpressionAST field,
            ExpressionAST def) {
        super(token);
        this.field = field;
        this.def = def;
    }

    public ExpressionAST getField() {
        return field;
    }

    public ExpressionAST getDefault() {
        return def;
    }

    public FieldSelection compile(DataType bound, ParserContext ctx,
            AttributeOrder ord) {
        Constant defComp = def.evalConstant(ctx);
        // Default value type is used to assist in the type inference process
        bound = DataType.strictest(bound, defComp.getType());
        Expression fieldComp = field.compile(bound, ctx, ord);
        return new FieldSelection(fieldComp, defComp.getValue());
    }

}
