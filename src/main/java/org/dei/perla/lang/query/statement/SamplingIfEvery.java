package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;

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
            Refresh refresh, List<Attribute> atts) {
        this.ifevery = ifevery;
        this.ratePolicy = ratePolicy;
        this.refresh = refresh;
        this.atts = atts;
    }

    public IfEvery getIfEvery() {
        return ifevery;
    }

    public List<Attribute> getAttributes() {
        return atts;
    }

    public RatePolicy getRatePolicy() {
        return ratePolicy;
    }

    public Refresh getRefresh() {
        return refresh;
    }

}
