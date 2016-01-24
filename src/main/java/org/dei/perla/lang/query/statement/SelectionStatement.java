package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class SelectionStatement implements Statement {

    private final Select select;
    private final GroupBy groupBy;
    private final Sampling sampling;
    private final Expression where;
    private final ExecutionConditions execCond;
    private final WindowSize terminate;

    // List of attributes required to run the data management clauses and the
    // having expression of the sampling clause
    private final List<Attribute> atts;

    /**
     * Creates an executable selection statement
     *
     * @param select selection clause
     * @param sampling sampling clause
     * @param where where clause
     * @param cond execution execution condition clause
     * @param terminate terminate after clause
     */
    public SelectionStatement(Select select,
            List<Attribute> atts,
            GroupBy groupBy,
            Sampling sampling,
            Expression where,
            ExecutionConditions execCond,
            WindowSize terminate) {
        this.select = select;
        this.groupBy = groupBy;
        this.atts = Collections.unmodifiableList(atts);
        this.sampling = sampling;
        this.where = where;
        this.execCond = execCond;
        this.terminate = terminate;
    }

    public List<Attribute> getAttributes() {
        return atts;
    }

    public Select getSelect() {
        return select;
    }

    public GroupBy getGroupBy() {
        return groupBy;
    }

    public Sampling getSampling() {
        return sampling;
    }

    public Expression getWhere() {
        return where;
    }

    public ExecutionConditions getExecutionConditions() {
        return execCond;
    }

    public WindowSize getTerminate() {
        return terminate;
    }

}
