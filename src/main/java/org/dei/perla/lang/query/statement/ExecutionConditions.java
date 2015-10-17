package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;

import java.util.Collections;
import java.util.List;

/**
 * Execution Conditions clause
 *
 * @author Guido Rota 16/03/15.
 */
public final class ExecutionConditions {

    public static final ExecutionConditions ALL_NODES =
            new ExecutionConditions(Collections.emptyList(),
                    Constant.TRUE, Collections.emptyList(), Refresh.NEVER);

    private final Expression cond;
    private final List<Attribute> specs;
    private final Refresh refresh;

    private final List<Attribute> atts;

    /**
     * Creates a new {@code ExecutionConditions} object
     *
     * @param specs list of attributes that the {@link Fpc} must have to be
     *              considered for the execution of the query
     * @param cond execution condition
     * @param atts attributes required to evaluate the execution condition
     * @param refresh refresh clause
     */
    public ExecutionConditions(List<Attribute> specs, Expression cond,
            List<Attribute> atts, Refresh refresh) {
        this.cond = cond;
        this.specs = specs;
        this.refresh = refresh;
        this.atts = Collections.unmodifiableList(atts);
    }

    public List<Attribute> getAttributes() {
        return atts;
    }

    public Expression getCondition() {
        return cond;
    }

    public List<Attribute> getSpecs() {
        return specs;
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
