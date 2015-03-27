package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.Sampling;

/**
 * @author Guido Rota 24/03/15.
 */
public final class Sampler {

    private final Fpc fpc;
    private final Sampling sampling;

    private Task sampleTask;
    private Task eventTask;

    protected Sampler(Sampling sampling, Fpc fpc)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(sampling.isComplete(),
                "Sampling clause is not complete.");
        Conditions.checkIllegalArgument(sampling.hasErrors(),
                "Sampling clause contains errors.");

        this.fpc = fpc;
        this.sampling = sampling;
    }

}
