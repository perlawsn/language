package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.LogicValue;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ConstantAST extends ExpressionAST {

    public static final ConstantAST TRUE =
            new ConstantAST(null, TypeClass.BOOLEAN, LogicValue.TRUE);
    public static final ConstantAST FALSE =
            new ConstantAST(null, TypeClass.BOOLEAN, LogicValue.FALSE);
    public static final ConstantAST NULL =
            new ConstantAST(null, TypeClass.ANY, null);

    private final TypeClass type;
    private final Object value;

    public ConstantAST(Token token, TypeClass type, Object value) {
        super(token);
        this.type = type;
        this.value = value;
    }

    public TypeClass getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean inferType(TypeVariable type, ParseContext ctx) {
        return type.restrict(this.type);
    }

}
