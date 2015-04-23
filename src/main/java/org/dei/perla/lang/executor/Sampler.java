package org.dei.perla.lang.executor;

/**
 * A simple marker interface for the Sampler query execution object
 *
 * @author Guido Rota 14/04/15.
 */
public interface Sampler {

    // Exceptions
    public static final String IFE_INIT_ERROR = "Initialization of IF EVERY " +
            "sampling failed, cannot retrieve sample the required attributes";

    public static final String IFE_SAMPLING_ERROR = "Sampling of IF EVERY " +
            "sampling attributes failed";

    public static final String EVT_INIT_ERROR = "Initialization of REFRESH " +
            "ON EVENT sampling failed, cannot retrieve the required events";

    public static final String EVT_SAMPLING_ERROR =
            "Sampling of REFRESH ON EVENT events failed";

    public static final String EVT_STOPPED_ERROR =
            "REFRESH ON EVENT sampling stopped prematurely";

    public static final String SAMP_STOPPED_ERROR =
            "Sampling operation stopped prematurely";

    public static final String SAMP_ERROR = "Unexpected error while sampling";

    /**
     * Starts the sampling operation.
     *
     * @throws QueryException if the sampling operation cannot be started
     */
    public void start() throws QueryException;

    /**
     * Stops the sampling operation
     */
    public void stop();

    public boolean isRunning();

}
