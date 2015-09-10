package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An interface representing a generic {@code Expression} node.
 *
 * @author Guido Rota 23/02/15.
 */
public abstract class Expression {

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
    public abstract DataType getType();

    /**
     * Runs the expression on the data generated by the Fpc.
     *
     * @param sample sample containing the attribute data to be used for
     *               computing the expression.
     * @param buffer complete {@link Buffer} view used to compute aggregates
     *
     * @return result of the computation.
     */
    public abstract Object run(Object[] sample, BufferView buffer);

    @Override
    public final String toString() {
        StringBuilder bld = new StringBuilder();
        buildString(bld);
        return bld.toString();
    }

    protected abstract void buildString(StringBuilder bld);

}
