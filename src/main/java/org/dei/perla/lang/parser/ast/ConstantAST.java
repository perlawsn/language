package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.util.Map;

/**
 * A constant value in the Abstract Syntax Tree.
 *
 * @author Guido Rota 30/07/15.
 */
public final class ConstantAST extends ExpressionAST {

    public static final ConstantAST TRUE =
            new ConstantAST(LogicValue.TRUE, TypeClass.BOOLEAN);
    public static final ConstantAST FALSE =
            new ConstantAST(LogicValue.FALSE, TypeClass.BOOLEAN);
    public static final ConstantAST NULL =
            new ConstantAST(null, TypeClass.ANY);

    private final TypeClass type;
    private final Object value;

    public ConstantAST(Object value, TypeClass type) {
        super();
        if (value != null && !type.isConcrete()) {
            throw new IllegalArgumentException("Type class must be concrete");
        }
        this.type = type;
        this.value = value;
    }

    public ConstantAST(Token token, TypeClass type, Object value) {
        super(token);
        if (value != null && !type.isConcrete()) {
            throw new IllegalArgumentException("Type class must be concrete");
        }
        this.type = type;
        this.value = value;
    }

    @Override
    protected void setType(TypeVariable type) {
        throw new IllegalStateException("Cannot set type to ConstantAST node");
    }

    @Override
    public TypeClass getTypeClass() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    @Override
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
        return bound.restrict(this.type);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        if (value == null) {
            return Constant.NULL;
        }
        return Constant.create(value, type.toDataType());
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantAST)) {
            return false;
        }

        ConstantAST oc = (ConstantAST) o;
        return oc.type == type && oc.value.equals(value);
    }

}
