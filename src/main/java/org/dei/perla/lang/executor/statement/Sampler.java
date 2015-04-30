package org.dei.perla.lang.executor.statement;

import org.dei.perla.lang.executor.QueryException;

/**
 * A simple marker interface for the Sampler query execution object
 *
 * @author Guido Rota 14/04/15.
 */
public interface Sampler {

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
