package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 24/03/15.
 */
public final class Sampling implements Clause {

    private final IfEvery ifevery;
    private final Refresh refresh;
    private final Expression where;
    private final boolean err;

    private Sampling(IfEvery ifevery, Refresh refresh, Expression where,
            boolean err) {
        this.ifevery = ifevery;
        this.refresh = refresh;
        this.where = where;
        this.err = err;
    }

    public static ClauseWrapper<Sampling> create(IfEvery ifevery, Refresh
            refresh, Expression where) {
        boolean err = false;
        String emsg = null;

        if (where != null && where.getType() != null &&
                where.getType() != DataType.BOOLEAN) {
            err = true;
            emsg = "Incompatible data type, WHERE condition must be boolean.";
        }

        Sampling s = new Sampling(ifevery, refresh, where, err);
        return new ClauseWrapper<>(s, emsg);
    }

    public IfEvery getIfEvery() {
        return ifevery;
    }

    public Refresh getRefresh() {
        return refresh;
    }

    public Expression getWhere() {
        return where;
    }

    @Override
    public boolean hasErrors() {
        if (where != null && where.hasErrors()) {
            return true;
        }
        return ifevery.hasErrors();
    }

    @Override
    public boolean isComplete() {
        if (where != null && !where.isComplete()) {
            return false;
        } else if (refresh != null && !refresh.isComplete()) {
            return false;
        }
        return ifevery.isComplete();
    }

    public Clause bind(Collection<Attribute> atts, List<Attribute> bound) {
        IfEvery bifevery = ifevery.bind(atts, bound);

        Refresh brefresh = null;
        if (refresh != null) {
            brefresh = refresh.bind(atts, bound);
        }

        Expression bwhere = null;
        if (where != null) {
            bwhere = where.bind(atts, bound);
        }

        ClauseWrapper<Sampling> cw =
                Sampling.create(bifevery, brefresh, bwhere);
        return cw.getClause();
    }

}
