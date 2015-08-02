package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * A generic expression node of the Abstract Syntax Tree. All concrete
 * expression nodes inherit from this class.
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class ExpressionAST extends NodeAST {

    private TypeVariable type;

    public ExpressionAST() {
        super();
    }

    public ExpressionAST(Token token) {
        super(token);
    }

    protected void setType(TypeVariable type) {
        if (this.type != null) {
            throw new IllegalStateException("Type has already been set");
        }
        this.type = type;
    }

    public TypeClass getTypeClass() {
        if (type == null) {
            throw new IllegalStateException("Cannot return, no type set");
        }
        return type.getTypeClass();
    }

    /**
     * Traverses the expression tree to infer the data type of the fields.
     * Type errors and inconsistencies are reported through the {@link Errors}
     * object in the {@link ParserContext}.
     *
     * @param bound type constraint, designates the desired type (or type
     *             class) of the expression being analyzed. This information is
     *             used by the inference algorithm to perform the type analysis.
     * @param ctx context object used to store intermediate results and errors
     * @return true if no type errors were found, false otherwise.
     */
    public abstract boolean inferType(TypeVariable bound, ParserContext ctx);

    /**
     * Returns a string message that can be used to report the occurrence of
     * a type error.
     *
     * @param operator textual description of the operator that generated the
     *                 error
     * @param position textual description of the approximate position in the
     *                 source code where the error has originated
     * @param expected expected type
     * @param found type found
     * @return textual description of the type error
     */
    protected String typeErrorString(String operator, String position,
            TypeClass expected, TypeClass found) {
        String msg = "Incompatible types: operator " + operator + " of" +
                " type '" + found + "' found at " + position  + "where an " +
                "operation of type '" + expected + "' was required.";
        return msg;
    }

}
