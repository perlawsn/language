package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BindingException;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class Selection implements Statement {

    private final Select select;
    private final WindowSize every;
    private final Sampling sampling;
    private final Expression where;
    private final ExecutionConditions cond;
    private final WindowSize terminate;

    // List of attributes required to run the data management clauses and the
    // where expression of the sampling clause
    private final List<Attribute> selAtts;

    public Selection(Select select, WindowSize every, Sampling sampling,
            Expression where, ExecutionConditions cond, WindowSize terminate) {
        this.select = select;
        this.every = every;
        this.sampling = sampling;
        this.where = where;
        this.cond = cond;
        this.terminate = terminate;
        selAtts = Collections.emptyList();
    }

    private Selection(Select select, List<Attribute> selAtts, WindowSize every,
            Sampling sampling, Expression where, ExecutionConditions cond,
            WindowSize terminate) {
        this.select = select;
        this.selAtts = Collections.unmodifiableList(selAtts);
        this.every = every;
        this.sampling = sampling;
        this.where = where;
        this.cond = cond;
        this.terminate = terminate;
    }

    public List<Attribute> getSelectAttributes() {
        return selAtts;
    }

    @Override
    public boolean isComplete() {
        return select.isComplete() && sampling.isComplete();
    }

    public Selection bind(Collection<Attribute> atts) throws BindingException {
        Errors err = new Errors();

        List<Attribute> selAtts = new ArrayList<>();
        Select bselect = select.bind(atts, selAtts, err);
        Sampling bsampling = sampling.bind(atts, err);
        Expression bwhere = where.bind(atts, selAtts, err);
        ExecutionConditions bcond = cond.bind(atts, err);

        if (!err.isEmpty()) {
            throw new BindingException(err.asString());
        }

        return new Selection(bselect, selAtts, every, bsampling, bwhere, bcond,
                terminate);
    }

    public List<Object[]> select(BufferView buffer) {
        return select.select(buffer);
    }

}
