package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.AttributeReference;

import java.util.Map;

/**
 * A reference to an {@link Fpc} attribute.
 *
 * @author Guido Rota 30/07/15.
 */
public final class AttributeReferenceAST extends ExpressionAST {

    private final String id;
    private final TypeVariable type;

    public AttributeReferenceAST(String id, TypeClass type) {
        this(null, id, type);
    }

    public AttributeReferenceAST(Token token, String id, TypeClass type) {
        super(token);
        this.id = id;
        this.type = new TypeVariable(type);
    }

    public String getId() {
        return id;
    }

    /**
     * Creates the {@link Attribute} object referenced by this {@code
     * AttributeReferenceAST}.
     *
     * @return attribute object referenced by this object
     * @throws IllegalStateException if the type class associated with the
     * reference is not concreate
     */
    public Attribute toAttribute() {
        TypeClass t = type.getTypeClass();
        if (!t.isConcrete()) {
            throw new IllegalStateException("TypeClass is not concrete");
        }
        return Attribute.create(id, t.toDataType());
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
    protected boolean inferType(TypeVariable bound, ParserContext ctx) {
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

    @Override
    protected Expression toExpression(ParserContext ctx, Map<Attribute, Integer> atts) {
        TypeClass tc = type.getTypeClass();
        if (!tc.isConcrete()) {
            String msg = "Cannot compile, attribute '" + id + "' at " +
                    getPosition() + " of type class " + tc + " cannot map " +
                    "to a concrete PerLa data type";
            ctx.addError(msg);
            return Constant.NULL;
        }

        Attribute att = Attribute.create(id, type.getTypeClass().toDataType());
        Integer idx = atts.get(att);
        if (idx == null) {
            idx = atts.size();
            atts.put(att, idx);
        }
        return new AttributeReference(id, tc.toDataType(), idx);
    }

}
