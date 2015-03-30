package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Guido Rota 24/03/15.
 */
public final class SamplingIfEvery implements Sampling {

    private final IfEvery ifevery;
    private final RatePolicy ratePolicy;
    private final Refresh refresh;

    public SamplingIfEvery(IfEvery ifevery, RatePolicy ratePolicy,
            Refresh refresh) {
        this.ifevery = ifevery;
        this.ratePolicy = ratePolicy;
        this.refresh = refresh;
    }

    public IfEvery getIfEvery() {
        return ifevery;
    }

    public RatePolicy getRatePolicy() {
        return ratePolicy;
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

    public SamplingIfEvery bind(Collection<Attribute> atts) {
        IfEvery bifevery = ifevery.bind(atts, new ArrayList<>());

        Refresh brefresh = null;
        if (refresh != null) {
            brefresh = refresh.bind(atts);
        }

        return new SamplingIfEvery(bifevery, ratePolicy, brefresh);
    }

}
