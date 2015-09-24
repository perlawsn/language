package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

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
    private final List<Attribute> specs;
    private final Refresh refresh;

    private final List<Attribute> atts;

    private ExecutionConditions(Expression cond, List<Attribute> specs,
            Refresh refresh) {
        this(cond, specs, refresh, Collections.emptyList());
    }

    private ExecutionConditions(Expression cond, List<Attribute> specs,
            Refresh refresh, List<Attribute> atts) {
        this.cond = cond;
        this.specs = specs;
        this.refresh = refresh;
        this.atts = Collections.unmodifiableList(atts);
    }

    public static ExecutionConditions create(Expression cond,
            List<Attribute> specs, Refresh refresh, Errors err) {
        DataType ct = cond.getType();
        if (ct != null && ct != DataType.BOOLEAN) {
            err.addError("Execution condition must be of type BOOLEAN");
        }

        if (cond instanceof Constant &&
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

    public List<Attribute> getSpecs() {
        return specs;
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
