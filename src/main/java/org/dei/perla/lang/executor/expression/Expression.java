package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * An interface representing a generic {@code Expression} node.
 *
 * <p>
 * PerLa expression nodes are divided in two categories: complete and
 * incomplete.
 * Complete nodes are fully specified expression, whose result can always be
 * computed using the {@code run} method. Complete nodes are always
 * associated with a {@link DataType} value that represents the type of the
 * expression result.
 *
 * <p>
 * Incomplete nodes, on the other hand, are not fully specified. This type of
 * node allows the parser to deal with uncertainty when parsing Fpc attribute
 * identifiers, whose specific type is only known after the query itself has
 * been parsed. Incomplete nodes may not have a {@link DataType}, and their
 * execution may lead to undefined behaviour.
 *
 * <p>
 * Incomplete nodes can be reified into complete nodes using the {@code
 * bind} method. This method takes as argument the list of attributes
 * available in a specific FPC, and uses it to bind the fields requested
 * in the query with the actual attributes exported by the Fpc where the
 * query is to be run.
 *
 * <p>
 * It is important to note that the bind method is not guaranteed to always
 * return a complete {@code Expression} node; this situation may present
 * itself when the original incomplete expression requires one or more
 * attributes that are not generated by the selected Fpc.
 *
 * <p>
 * The user is advised to check whether the {@code Expression} is complete
 * and correct before computing its result.
 *
 * @author Guido Rota 23/02/15.
 */
public interface Expression {

    /**
     * Returns the result type of this {@code Expression}.
     *
     * <p>
     * This method returns {@code null} if the {@code Expression} is not
     * complete.
     *
     * @return result type of the {@code Expression}, {@code null} if the
     * expression is not complete.
     */
    public DataType getType();

    /**
     * Indicates if the {@code Expression} is complete, and can therefore be
     * run.
     *
     * @return true if the {@code Expression} is complete, false otherwise.
     */
    public boolean isComplete();

    /**
     * Indicates if the {@code Expression} contains any errors, which are
     * mostly due to incompatible data types.
     *
     * @return true if the {@code Expression} contains errors, false otherwise.
     */
    public boolean hasErrors();

    /**
     * Binds the {@code Expression} to the attributes generated by a specific
     * Fpc. This method has no effect on complete {@code Expression}s.
     *
     * <p>
     * The bound expression may contain errors if the type of the attributes
     * passed as parameter are incompatible with the operations being performed.
     *
     * @param atts List of {@link Attribute} generated by the Fpc
     * @return new {@code Expression} instance bound to the data attributes
     * passed as parameter.
     */
    public Expression bind(List<Attribute> atts);

    /**
     * Runs the expression on the data generated by the Fpc.
     *
     * @param record record containing the attribute data to be used for
     *               computing the expression.
     * @param buffer complete {@link Buffer} view used to compute aggregates
     *
     * @return result of the computation.
     */
    public Object run(Object[] record, BufferView buffer);

}
