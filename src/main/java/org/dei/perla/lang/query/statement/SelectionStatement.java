package org.dei.perla.lang.query.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.BindingException;
import org.dei.perla.lang.query.expression.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
    // where expression of the sampling clause
    private final List<Attribute> dataAtts;

    /**
     *
     * @param select
     * @param every
     * @param sampling
     * @param where
     * @param cond
     * @param terminate terminate condition. A WindowSize.ZERO value
     *                  indicates that the query never terminates.
     */
    public SelectionStatement(Select select, WindowSize every, Sampling sampling,
            Expression where, ExecutionConditions cond, WindowSize terminate) {
        this.select = select;
        this.every = every;
        this.sampling = sampling;
        this.where = where;
        this.cond = cond;
        this.terminate = terminate;
        dataAtts = Collections.emptyList();
    }

    private SelectionStatement(Select select, List<Attribute> dataAtts,
            WindowSize every, Sampling sampling, Expression where,
            ExecutionConditions cond, WindowSize terminate) {
        this.select = select;
        this.dataAtts = Collections.unmodifiableList(dataAtts);
        this.every = every;
        this.sampling = sampling;
        this.where = where;
        this.cond = cond;
        this.terminate = terminate;
    }

    public List<Attribute> getDataAttributes() {
        return dataAtts;
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

    public SelectionStatement bind(Collection<Attribute> atts) throws BindingException {
        Errors err = new Errors();

        List<Attribute> dataAtts = new ArrayList<>();
        Select bselect = select.bind(atts, dataAtts, err);
        Expression bwhere = where.bind(atts, dataAtts, err);

        Sampling bsampling = sampling.bind(atts, err);
        ExecutionConditions bcond = cond.bind(atts, err);

        if (!err.isEmpty()) {
            throw new BindingException(err.asString());
        }

        // Add timestamp attribute if not explicitly included in the query
        if (!dataAtts.contains(Attribute.TIMESTAMP)) {
            dataAtts.add(Attribute.TIMESTAMP);
        }

        return new SelectionStatement(bselect, dataAtts, every, bsampling, bwhere, bcond,
                terminate);
    }

    public List<Object[]> select(BufferView buffer) {
        return select.select(buffer);
    }

}
