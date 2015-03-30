package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 24/03/15.
 */
public final class SamplingIfEvery implements Sampling {

    private final IfEvery ifevery;
    private final UnsupportedSamplingRate usr;
    private final Refresh refresh;

    public SamplingIfEvery(IfEvery ifevery, UnsupportedSamplingRate usr,
            Refresh refresh) {
        this.ifevery = ifevery;
        this.usr = usr;
        this.refresh = refresh;
    }

    public IfEvery getIfEvery() {
        return ifevery;
    }

    public Refresh getRefresh() {
        return refresh;
    }

    @Override
    public boolean hasErrors() {
        return ifevery.hasErrors();
    }

    @Override
    public boolean isComplete() {
        if (refresh != null && !refresh.isComplete()) {
            return false;
        }
        return ifevery.isComplete();
    }

    public SamplingIfEvery bind(Collection<Attribute> atts, List<Attribute> bound) {
        List<Attribute> sampBound = new ArrayList<>();
        IfEvery bifevery = ifevery.bind(atts, sampBound);

        Refresh brefresh = null;
        if (refresh != null) {
            brefresh = refresh.bind(atts);
        }

        return new SamplingIfEvery(bifevery, usr, brefresh);
    }

}
