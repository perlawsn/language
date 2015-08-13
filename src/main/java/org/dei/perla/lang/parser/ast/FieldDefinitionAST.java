package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class FieldDefinitionAST extends NodeAST {

    private final String name;
    private final DataType type;
    private final ExpressionAST defValue;

    public FieldDefinitionAST(String name, DataType type,
            ExpressionAST defValue) {
        this(null, name, type, defValue);
    }

    public FieldDefinitionAST(Token token, String name,
            DataType type, ExpressionAST defValue) {
        super(token);
        this.name = name;
        this.type = type;
        this.defValue = defValue;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public ExpressionAST getDefault() {
        return defValue;
    }

}
