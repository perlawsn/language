package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * A reference to an {@link Fpc} attribute.
 *
 * @author Guido Rota 30/07/15.
 */
public final class AttributeReferenceAST extends ExpressionAST {

    private final String id;
    private final TypeVariable type;

    public AttributeReferenceAST(String id, TypeClass type) {
        super();
        this.id = id;
        this.type = new TypeVariable(type);
    }

    public AttributeReferenceAST(Token token, String id, TypeClass type) {
        super(token);
        this.id = id;
        this.type = new TypeVariable(type);
    }

    public String getIdentifier() {
        return id;
    }

    public TypeVariable getType() {
        return type;
    }

    @Override
    public boolean inferType(TypeVariable bound, ParserContext ctx) {
        boolean res = true;

        TypeClass prev = type.getTypeClass();
        TypeClass curr = bound.getTypeClass();
        if (!TypeVariable.merge(type, bound)) {
            String msg = "Incompatible type for attribute '" + id + "': " +
                    "usage at " + getPosition() + " of type '" + type + " is " +
                    "not compatible with inferred type '" + bound + "'.";
            ctx.addError(msg);
            res = false;
        }

        return res && ctx.addAttributeReference(this);
    }

}
