package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParseContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;

/**
 * A generic expression node of the Abstract Syntax Tree. All concrete
 * expression nodes inherit from this class.
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class ExpressionAST extends NodeAST {

    protected ExpressionAST() {
        super();
    }

    public ExpressionAST(Token token) {
        super(token);
    }

    /**
     * Traverses the expression tree to infer the data type of the fields.
     * Type errors and inconsistencies are reported through the {@link Errors}
     * object in the {@link ParseContext}.
     *
     * @param type type constraint, designates the desired type (or type
     *             class) of the expression being analyzed. This information is
     *             used by the inference algorithm to perform the type analysis.
     * @param ctx context object used to store intermediate results and errors
     * @return true if no type errors were found, false otherwise.
     */
    public abstract boolean inferType(TypeVariable type, ParseContext ctx);

}
