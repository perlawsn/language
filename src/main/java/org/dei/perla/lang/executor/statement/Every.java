package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.time.temporal.TemporalUnit;
import java.util.List;

/**
 * @author Guido Rota 23/03/15.
 */
public final class Every implements Clause {

    private final Expression e;
    private final TemporalUnit tu;
    private boolean err;

    private Every(Expression e, TemporalUnit tu, boolean err) {
        this.e = e;
        this.tu = tu;
        this.err = err;
    }

    public static ClauseWrapper<Every> create(Expression e, TemporalUnit tu) {
        String err = null;

        DataType t = e.getType();
        if (t != null && t != DataType.INTEGER && t != DataType.FLOAT) {
            err = "Incompatible data type, period must be a numeric value";
        }

        return null;
    }

    @Override
    public boolean hasErrors() {
        return e.hasErrors() || err;
    }

    @Override
    public boolean isComplete() {
        return e.isComplete();
    }

    @Override
    public Every bind(List<Attribute> atts) {
        if (isComplete() || hasErrors()) {
            return this;
        }
        return new Every(e.bind(atts), tu, err);
    }

}
