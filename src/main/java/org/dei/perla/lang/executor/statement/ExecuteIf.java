package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    @Override
    public Set<String> getFields() {
        Set<String> fields = new TreeSet<>();
        fields.addAll(cond.getFields());
        if (refresh != null) {
            fields.addAll(refresh.getFields());
        }
        return fields;
    }

    @Override
    public ExecuteIf bind(List<Attribute> atts) {
        if (isComplete()) {
            return this;
        }

        Expression newCond = cond.bind(atts);
        Refresh newRef = refresh.bind(atts);
        return new ExecuteIf(newCond, newRef);
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
