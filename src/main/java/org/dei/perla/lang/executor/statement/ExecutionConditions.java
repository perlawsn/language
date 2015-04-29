package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.expression.Constant;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.LogicValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 16/03/15.
 */
public final class ExecutionConditions {

    public static final ExecutionConditions ALL_NODES =
            new ExecutionConditions(Constant.TRUE, Collections.emptyList(),
                    Refresh.NEVER);

    private final Expression cond;
    private final List<DataTemplate> specs;
    private final Refresh refresh;

    private final List<Attribute> atts;

    private ExecutionConditions(Expression cond, List<DataTemplate> specs,
            Refresh refresh) {
        this.cond = cond;
        this.specs = specs;
        this.refresh = refresh;
        atts = Collections.emptyList();
    }

    private ExecutionConditions(Expression cond, List<DataTemplate> specs,
            Refresh refresh, List<Attribute> atts) {
        this.cond = cond;
        this.specs = specs;
        this.refresh = refresh;
        this.atts = Collections.unmodifiableList(atts);
    }

    public static ExecutionConditions create(Expression cond,
            List<DataTemplate> specs, Refresh refresh, Errors err) {
        DataType ct = cond.getType();
        if (ct != null && ct != DataType.BOOLEAN) {
            err.addError("Execution condition must be of type BOOLEAN");
        }

        if (cond != null && cond instanceof Constant &&
                ((LogicValue) cond.run(null, null)).toBoolean() == false) {
            err.addError("Execution condition always evaluates to false");
            return null;
        }

        return new ExecutionConditions(cond, specs, refresh);
    }

    public List<Attribute> getAttributes() {
        return atts;
    }

    public Expression getCondition() {
        return cond;
    }

    public List<DataTemplate> getSpecs() {
        return specs;
    }

    public Refresh getRefresh() {
        return refresh;
    }

    public boolean isComplete() {
        return cond.isComplete() && refresh.isComplete();
    }

    public ExecutionConditions bind(Collection<Attribute> atts, Errors err) {
        List<Attribute> bound = new ArrayList<>();
        Expression bcond = cond.bind(atts, bound, err);
        Refresh brefresh = refresh.bind(atts);
        return new ExecutionConditions(bcond, specs, brefresh, bound);
    }

}
