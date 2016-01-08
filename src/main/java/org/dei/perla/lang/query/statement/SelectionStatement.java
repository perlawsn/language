package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Guido Rota 04/03/15.
 */
public final class SelectionStatement implements Statement {

    private final Select select;
    private final WindowSize every;
    private final Sampling sampling;
    private final Expression where;
    private final ExecutionConditions cond;
    private final WindowSize terminate;

    // List of attributes required to run the data management clauses and the
    // having expression of the sampling clause
    private final List<Attribute> atts;

    /**
     * Creates an executable selection statement
     *
     * @param select select clause
     * @param every
     * @param sampling
     * @param where
     * @param cond
     * @param terminate
     */
    public SelectionStatement(Select select,
            List<Attribute> atts,
            WindowSize every,
            Sampling sampling,
            Expression where,
            ExecutionConditions cond,
            WindowSize terminate) {
        this.select = select;
        this.atts = Collections.unmodifiableList(atts);
        this.every = every;
        this.sampling = sampling;
        this.where = where;
        this.cond = cond;
        this.terminate = terminate;
    }

    public List<Attribute> getAttributes() {
        return atts;
    }

    public Select getSelect() {
        return select;
    }

    public WindowSize getEvery() {
        return every;
    }

    public Sampling getSampling() {
        return sampling;
    }

    public Expression getWhere() {
        return where;
    }

    public ExecutionConditions getExecutionConditions() {
        return cond;
    }

    public WindowSize getTerminate() {
        return terminate;
    }

    public List<Object[]> select(BufferView buffer) {
        return select.select(buffer);
    }

}
