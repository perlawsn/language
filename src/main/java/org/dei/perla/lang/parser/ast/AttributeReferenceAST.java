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

    @Override
    protected void setType(TypeVariable type) {
        throw new IllegalStateException(
                "Cannot set type to AttributeReferenceAST node");
    }

    @Override
    public TypeClass getTypeClass() {
        return type.getTypeClass();
    }

    /**
     * Merges the type of this attribute reference with the type of another
     * attribute reference. This method is intended to be used by the {@link
     * ParserContext} during the type inference process.
     *
     * @param other object to merge with
     * @return true if the merge was successful, false if the type of the
     * other {@code AttributeReferenceAST} is not compatible with this
     * reference's type.
     */
    public boolean mergeTypes(AttributeReferenceAST other) {
        TypeVariable t1 = type;
        TypeVariable t2 = other.type;
        return TypeVariable.merge(t1, t2);
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
