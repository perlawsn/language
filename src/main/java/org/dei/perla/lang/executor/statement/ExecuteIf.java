package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 16/03/15.
 */
public final class ExecuteIf implements Clause {

    private final Expression cond;
    private final Refresh refresh;

    public ExecuteIf(Expression cond, Refresh refresh) {
        this.cond = cond;
        this.refresh = refresh;
    }

    public Expression getCondition() {
        return cond;
    }

    @Override
    public boolean hasErrors() {
        return cond.hasErrors() || refresh.hasErrors();
    }

    @Override
    public boolean isComplete() {
        return cond.isComplete() && refresh.isComplete();
    }

    public ExecuteIf bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression bcond = cond.bind(atts, bound);
        Refresh brefresh = null;
        if (refresh != null) {
            brefresh = refresh.bind(atts);
        }
        return new ExecuteIf(bcond, brefresh);
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
