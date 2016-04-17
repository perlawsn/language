package org.dei.perla.lang.query.statement;

import org.dei.perla.core.fpc.Attribute;

import java.util.List;

/**
 * Executable SAMPLING IF EVERY clause
 *
 * @author Guido Rota 24/03/15.
 */
public final class SamplingIfEvery implements Sampling {

    private final List<IfEvery> ifevery;
    private final List<Attribute> atts;
    private final RatePolicy ratePolicy;
    private final Refresh refresh;

    /**
     * Creates a new executable SAMPLING IF EVERY clause object
     *
     * @param ifevery list of {@link IfEvery} conditions
     * @param atts list of attibutes that must be sampled to evaluate the
     *             if-every conditions
     * @param ratePolicy sampling rate policy
     * @param refresh determines if and when the sampling frequency has to be
     *                re-evaluated after the sampler has started
     */
    public SamplingIfEvery(
            List<IfEvery> ifevery,
            List<Attribute> atts,
            RatePolicy ratePolicy,
            Refresh refresh) {
        this.ifevery = ifevery;
        this.ratePolicy = ratePolicy;
        this.refresh = refresh;
        this.atts = atts;
    }

    public List<IfEvery> getIfEvery() {
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
    
    public String toString(){
    	StringBuffer s = new StringBuffer("SAMPLING ");
    	ifevery.forEach(f -> s.append(f.toString() + " "));
    	if(refresh != Refresh.NEVER)
    		s.append("REFRESH " + refresh.toString());
    	return s.toString();
    }

}
