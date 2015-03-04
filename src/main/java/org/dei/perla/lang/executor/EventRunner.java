package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.executor.query.Query;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Guido Rota 04/03/15.
 */
public class EventRunner extends Runner {

    private final int samples;

    private int recvd;

    public EventRunner(Fpc fpc, Query query, int samples, QueryHandler qh) {
        super(fpc, query, qh);
        this.samples = samples;
        recvd = 0;
    }

    @Override
    protected void newSample() {
        recvd++;
        if (recvd == samples) {
            //TODO: execute the sampling operation
            throw new RuntimeException("unimplemented");
        }
    }

}
