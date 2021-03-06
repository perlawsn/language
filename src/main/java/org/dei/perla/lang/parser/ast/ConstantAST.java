package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

/**
 * A constant value in the Abstract Syntax Tree.
 *
 * @author Guido Rota 30/07/15.
 */
public final class ConstantAST extends ExpressionAST {

    public static final ConstantAST ZERO =
            new ConstantAST(0, DataType.INTEGER);
    public static final ConstantAST ONE =
            new ConstantAST(1, DataType.INTEGER);

    public static final ConstantAST TRUE =
            new ConstantAST(LogicValue.TRUE, DataType.BOOLEAN);
    public static final ConstantAST FALSE =
            new ConstantAST(LogicValue.FALSE, DataType.BOOLEAN);
    public static final ConstantAST NULL =
            new ConstantAST(null, DataType.ANY);

    private final DataType type;
    private final Object value;

    public ConstantAST(Object value, DataType type) {
        this(null, type, value);
    }

    public ConstantAST(Token token, DataType type, Object value) {
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
    public DataType getType() {
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
    protected Expression toExpression(ParserContext ctx, AttributeOrder ord) {
        if (value == null) {
            return Constant.NULL;
        }
        return Constant.create(value, type);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ConstantAST)) {
            return false;
        }

        ConstantAST oc = (ConstantAST) o;
        if (type != oc.type) {
            return false;
        } else if (value == null || oc.value == null) {
            return value == oc.value;
        } else {
            return value.equals(oc.value);
        }
    }

}
