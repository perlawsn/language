package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Collections;
import java.util.Map;

/**
 * A generic expression node of the Abstract Syntax Tree.
 *
 * <p>Expression compilation is performed in three steps:
 * <ul>
 *     <li>The AST parser reads input text and produces the abstract syntax
 *     tree representation of the expression. During this phase the
 *     expression is checked against some context-aware grammar rules that
 *     ensure that the expression is suitable to be used in the clause where
 *     it is declared. These rules forbid the declaration of expressions
 *     containing aggregations or attribute references in some clauses of the
 *     query (i.e., no aggregations are allowed in the SAMPLING clause, etc).
 *     For further information, see how the enum {@link ExpressionType} is
 *     employed in the JavaCC parser.
 *     </li>
 *     <li>The {@code inferType} method is invoked to infer the data type
 *     of all attribute references in the expression, and to check the type
 *     correctness of the entire expression. Depending on the expression,
 *     this phase may not pin down a concrete type for every referenced
 *     attribute.
 *     </li>
 *     <li>The {@code toExpression} method is invoked to create an
 *     executable expression from the parsed Abstract Syntax Tree. During
 *     this phase, additional type checks are performed to ensure that all
 *     expression operands are used appropriately (see {@link CompareAST} and
 *     {@link BetweenAST}). Moreover, attribute references whose inferred
 *     type class is not concrete are reported to the user, which will then be
 *     prompted to insert additional type annotations in the expression.
 *     </li>
 * </ul>
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class ExpressionAST extends NodeAST {

    private TypeVariable type;

    public ExpressionAST(Token token) {
        super(token);
    }

    /**
     * Sets the {@link TypeVariable} associated with this expression node. It
     * is the implementation's responsibility to check that the {@link
     * TypeClass} associated with the type variable is consistend with the
     * output type of the operation.
     *
     * @param type type variable linked to this node
     * @throws IllegalStateException if the method is called more than once
     */
    protected void setType(TypeVariable type) {
        if (this.type != null) {
            throw new IllegalStateException("Type has already been set");
        }
        this.type = type;
    }

    /**
     * Returns the output type of the expression node
     *
     * @return output type of the expression node
     * @throws IllegalStateException if no type variable is set
     */
    public TypeClass getTypeClass() {
        if (type == null) {
            throw new IllegalStateException("Cannot return, no type set");
        }
        return type.getTypeClass();
    }

    /**
     * Compiles the AST tree identified by this object into an executable
     * {@link Expression}.
     *
     * @param bound type constraint, designates the desired type (or type
     *             class) of the expression being analyzed. This information is
     *             used by the inference algorithm to perform the type analysis.
     * @param ctx context object used to store intermediate results and errors
     * @param atts map employed for storing the attribute binding order
     * @return expression object corresponding to the AST node
     */
    public Expression compile(TypeClass bound, ParserContext ctx,
            Map<Attribute, Integer> atts) {
        TypeVariable v = new TypeVariable(bound);
        boolean typeOk = inferType(v, ctx);
        if (!typeOk) {
            return Constant.NULL;
        }

        return toExpression(ctx, atts);
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
    protected abstract boolean inferType(TypeVariable bound, ParserContext ctx);

    /**
     * Creates an {@link Expression} object corresponding to the AST node.
     *
     * @param ctx context object used to store intermediate results and errors
     * @param atts map employed for storing the attribute binding order
     * @return expression object corresponding to the ExpressionAST node
     */
    protected abstract Expression toExpression(ParserContext ctx,
            Map<Attribute, Integer> atts);

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
        return "Incompatible types: operator " + operator + " of type '" +
                found + "' found at " + position  + "where an " +
                "operation of type '" + expected + "' was required.";
    }

    /**
     * Utility method employed to evaluate the value of an integer
     * ConstantAST. See {@link DurationWindowAST} and {@link SampleWindowAST}
     * for additional examples.
     *
     * @param ctx Context for the storage of intermediate parsing state
     * @return integer value of the {@link ConstantAST} node
     */
    protected int evalIntConstant(ParserContext ctx) {
        Expression e = compile(TypeClass.INTEGER, ctx,
                Collections.emptyMap());
        if (!(e instanceof Constant)) {
            throw new IllegalStateException("Parser bug found, WindowSize " +
                    "expression is not constant");
        }

        Constant c = (Constant) e;
        if (c.getValue() == null) {
            return 0;
        }
        return (Integer) c.getValue();
    }

}
