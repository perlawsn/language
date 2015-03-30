package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 24/03/15.
 */
public final class SamplingIfEvery implements Sampling {

    private final IfEvery ifevery;
    private final List<Attribute> atts;
    private final RatePolicy ratePolicy;
    private final Refresh refresh;

    public SamplingIfEvery(IfEvery ifevery, RatePolicy ratePolicy,
            Refresh refresh) {
        this.ifevery = ifevery;
        this.atts = Collections.emptyList();
        this.ratePolicy = ratePolicy;
        this.refresh = refresh;
    }

    private SamplingIfEvery(IfEvery ifevery, List<Attribute> atts,
            RatePolicy ratePolicy, Refresh refresh) {
        this.ifevery = ifevery;
        this.atts = Collections.unmodifiableList(atts);
        this.ratePolicy = ratePolicy;
        this.refresh = refresh;
    }

    public IfEvery getIfEvery() {
        return ifevery;
    }

    public List<Attribute> getIfEveryAttributes() {
        return atts;
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
        List<Attribute> bound = new ArrayList<>();
        IfEvery bifevery = ifevery.bind(atts, bound);

        Refresh brefresh = null;
        if (refresh != null) {
            brefresh = refresh.bind(atts);
        }

        return new SamplingIfEvery(bifevery, bound, ratePolicy, brefresh);
    }

}
