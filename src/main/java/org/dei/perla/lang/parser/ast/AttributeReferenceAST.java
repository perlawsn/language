package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.AttributeReference;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

/**
 * A reference to an {@link Fpc} attribute.
 *
 * @author Guido Rota 30/07/15.
 */
public final class AttributeReferenceAST extends ExpressionAST {

    private final String id;

    // Not final, as it changes when performing type inference
    private TypeVariable type;
    // Tracks if type inference has already been performed or not
    private boolean inferenced = false;

    public AttributeReferenceAST(String id, DataType type) {
        this(null, id, type);
    }

    public AttributeReferenceAST(Token token, String id, DataType type) {
        super(token);
        this.id = id;
        this.type = new TypeVariable(type);
    }

    public String getId() {
        return id;
    }

    @Override
    protected void setType(TypeVariable type) {
        throw new IllegalStateException(
                "Cannot set type to AttributeReferenceAST node");
    }

    @Override
    public DataType getType() {
        return type.getType();
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
        if (inferenced) {
            throw new IllegalStateException("Type inference already performed");
        }

        inferenced = true;
        boolean res = true;

        if (!bound.restrict(type.getType())) {
            String msg = "Incompatible type for attribute '" + id + "': " +
                    "usage at " + getPosition() + " of type '" + type + " is " +
                    "not compatible with inferred type '" + bound + "'.";
            ctx.addError(msg);
            res = false;
        }
        type = bound;

        return res && ctx.addAttributeReference(this);
    }

    @Override
    protected Expression toExpression(ParserContext ctx, AttributeOrder ord) {
        DataType tc = type.getType();
        if (!tc.isConcrete()) {
            String msg = "Cannot compile, attribute '" + id + "' at " +
                    getPosition() + " of type class " + tc + " cannot map " +
                    "to a concrete PerLa data type";
            ctx.addError(msg);
            return Constant.NULL;
        }

        int idx = ord.getIndex(id);
        return new AttributeReference(id, tc, idx);
    }

}
