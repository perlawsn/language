package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.LogicValue;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ConstantAST extends ExpressionAST {

    public static final ConstantAST TRUE =
            new ConstantAST(null, DataType.BOOLEAN, LogicValue.TRUE);
    public static final ConstantAST FALSE =
            new ConstantAST(null, DataType.BOOLEAN, LogicValue.FALSE);
    public static final ConstantAST NULL =
            new ConstantAST(null, null, null);

    private final DataType type;
    private final Object value;

    public ConstantAST(Token token, DataType type, Object value) {
        super(token);
        this.type = type;
        this.value = value;
    }

    public DataType getDataType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

}
