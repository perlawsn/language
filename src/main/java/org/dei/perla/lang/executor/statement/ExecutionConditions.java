package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 16/03/15.
 */
public final class ExecutionConditions implements Clause {

    private final Expression cond;
    private final List<DataTemplate> specs;
    private final Refresh refresh;

    public ExecutionConditions(Expression cond, List<DataTemplate> specs,
            Refresh refresh) {
        this.cond = cond;
        this.specs = specs;
        this.refresh = refresh;
    }

    public Expression getCondition() {
        return cond;
    }

    public List<DataTemplate> getSpecs() {
        return specs;
    }

    @Override
    public boolean hasErrors() {
        return cond.hasErrors() || refresh.hasErrors();
    }

    @Override
    public boolean isComplete() {
        return cond.isComplete() && refresh.isComplete();
    }

    public ExecutionConditions bind(Collection<Attribute> atts, List<Attribute> bound) {
        Expression bcond = cond.bind(atts, bound);
        Refresh brefresh = refresh.bind(atts);
        return new ExecutionConditions(bcond, specs, brefresh);
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
