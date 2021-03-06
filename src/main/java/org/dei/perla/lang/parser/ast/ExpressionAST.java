package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.TypeVariable;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

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
     * DataType} associated with the type variable is consistend with the
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
    public DataType getType() {
        if (type == null) {
            throw new IllegalStateException("Cannot return, no type set");
        }
        return type.getType();
    }

    /**
     * Compiles the AST tree identified by this object into an executable
     * {@link Expression}.
     *
     * @param bound type constraint, designates the desired type (or type
     *             class) of the expression being analyzed. This information is
     *             used by the inference algorithm to perform the type analysis.
     * @param ctx context object used to store intermediate results and errors
     * @param ord {@link Attribute} binding order
     * @return expression object corresponding to the AST node
     */
    public Expression compile(DataType bound, ParserContext ctx,
            AttributeOrder ord) {
        TypeVariable v = new TypeVariable(bound);
        boolean typeOk = inferType(v, ctx);
        if (!typeOk) {
            return Constant.NULL;
        }

        return toExpression(ctx, ord);
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
     * @param ord {@link Attribute} binding order
     * @return expression object corresponding to the ExpressionAST node
     */
    protected abstract Expression toExpression(ParserContext ctx,
            AttributeOrder ord);

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
            DataType expected, DataType found) {
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
     * @throws RuntimeException if the expression is not constant
     */
    public int evalIntConstant(ParserContext ctx) {
        Constant c = evalConstant(ctx);
        if (c.getType() != DataType.INTEGER) {
            throw new ClassCastException();
        }
        if (c.getValue() == null) {
            return 0;
        }
        return (Integer) c.getValue();
    }

    /**
     * Utility method employed to evaluate the value of a constant {@link
     * ExpressionAST}
     *
     * @param ctx context object employed to store intermediate parsin results
     * @return {@code Constant} object representing the value of the expression
     * @throws RuntimeException if the expression is not constant
     */
    public Constant evalConstant(ParserContext ctx) {
        if (!(this instanceof ConstantAST)) {
            throw new RuntimeException("Expression is not constant");
        }

        Expression e = compile(DataType.ANY, ctx,
                new AttributeOrder());
        return (Constant) e;
    }

}
