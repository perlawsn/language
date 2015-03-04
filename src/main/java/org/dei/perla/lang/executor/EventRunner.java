package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.executor.query.Query;

/**
 * @author Guido Rota 04/03/15.
 */
public class EventRunner extends Runner {

    private final int samples;

    public EventRunner(Fpc fpc, Query query, int samples, QueryHandler qh) {
        super(fpc, query, qh);
        this.samples = samples;
    }

}
