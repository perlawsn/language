package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.statement.*;

import java.util.List;

/**
 * @author Guido Rota 22/04/15.
 */
public class SelectExecutor {

    private final Fpc fpc;
    private final SelectionQuery query;
    private final Select select;

    private final Buffer buffer;

    private final Sampler sampler;
    private final SamplerHandler sampHand = new SamplerHandler();

    public SelectExecutor(Fpc fpc, SelectionQuery query) {
        this.fpc = fpc;
        this.query = query;
        select = query.getSelect();

        // TODO: forecast average buffer length
        buffer = new ArrayBuffer(query.getSelectAttributes(), 512);
        sampler = createSampler(query.getSampling(),
                query.getSelectAttributes());
    }

    private Sampler createSampler(Sampling samp, List<Attribute> atts) {
        if (samp instanceof SamplingIfEvery) {
            SamplingIfEvery sife = (SamplingIfEvery) samp;
            return new SamplerIfEvery(sife, atts, fpc, sampHand);

        } else if (samp instanceof SamplingEvent) {
            SamplingEvent sev = (SamplingEvent) samp;
            return new SamplerEvent(sev, atts, fpc, sampHand);

        } else {
            throw new IllegalArgumentException("Cannot start sampling of type" +
                    samp.getClass().getSimpleName());
        }
    }

    public void start() {
        throw new RuntimeException("unimplemented");
    }

    /**
     * Sampling handler class
     *
     * @author Guido Rota 22/04/2015
     */
    private class SamplerHandler implements QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable error) {
            // TODO: Escalate
        }

        @Override
        public void data(Sampling source, Object[] value) {
            buffer.add(value);
        }

    }

}
