package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.query.statement.*;

import java.util.List;

/**
 * A class for managing the execution of the execute if condition
 *
 * @author Guido Rota 01/10/15.
 */
public final class SamplerRunner {

    private final Fpc fpc;

    private final Sampling sampling;
    private final ExecutionConditions cond;
    private final QueryHandler<? super Sampling, Object[]> handler;

    private final Sampler sampler;

    public SamplerRunner(SelectionStatement query, Fpc fpc,
            QueryHandler<? super Sampling, Object[]> handler) {
        this.sampling = query.getSampling();
        this.cond = query.getExecutionConditions();
        this.fpc = fpc;
        this.handler = handler;
        sampler = createSampler(sampling, query.getAttributes(), handler);
    }

    private Sampler createSampler(Sampling samp, List<Attribute> atts,
            QueryHandler<? super Sampling, Object[]> handler)
            throws IllegalArgumentException {
        if (samp instanceof SamplingIfEvery) {
            SamplingIfEvery sife = (SamplingIfEvery) samp;
            return new SamplerIfEvery(sife, fpc, atts, handler);

        } else if (samp instanceof SamplingEvent) {
            SamplingEvent sev = (SamplingEvent) samp;
            return new SamplerEvent(sev, fpc, atts, handler);

        } else {
            throw new IllegalArgumentException("Cannot start sampling of type" +
                    samp.getClass().getSimpleName());
        }
    }

    public void start() {
        throw new RuntimeException("unimplemented");
    }

    public void stop() {
        throw new RuntimeException("unimplemented");
    }

}
