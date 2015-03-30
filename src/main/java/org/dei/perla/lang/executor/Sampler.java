package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.utils.Conditions;
import org.dei.perla.lang.executor.statement.SamplingIfEvery;

/**
 * @author Guido Rota 24/03/15.
 */
public final class Sampler {

    private final Fpc fpc;
    private final SamplingIfEvery sampling;

    private Task sampleTask;
    private Task eventTask;

    protected Sampler(SamplingIfEvery samplingIfEvery, Fpc fpc)
            throws IllegalArgumentException {
        Conditions.checkIllegalArgument(samplingIfEvery.isComplete(),
                "Sampling clause is not complete.");
        Conditions.checkIllegalArgument(samplingIfEvery.hasErrors(),
                "Sampling clause contains errors.");

        this.fpc = fpc;
        this.sampling = samplingIfEvery;
    }

}
