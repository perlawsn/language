package org.dei.perla.lang.executor.statement;

import org.dei.perla.lang.executor.expression.Expression;

/**
 * @author Guido Rota 16/03/15.
 */
public class ExecuteIf {

    private final Expression cond;
    private final Refresh refresh;

    public ExecuteIf(Expression cond, Refresh refresh) {
        this.cond = cond;
        this.refresh = refresh;
    }

    public Expression getCondition() {
        return cond;
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
