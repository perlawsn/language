package org.dei.perla.lang.executor.statement;

import org.dei.perla.lang.executor.QueryException;

/**
 * A simple marker interface for the Sampler query execution object
 *
 * @author Guido Rota 14/04/15.
 */
public interface Sampler {

    /**
     * Starts the execution of the {@link Sampling} clause. Startup errors
     * will be asynchronously notified through the {@link ClauseHandler}
     * specified in the constructor after the {@code start()} method is
     * finished.
     */
    public void start();

    /**
     * Stops executing the {@link Sampling} clause. The clause can be resumed
     * by invoking the {@code start()} method anew.
     */
    public void stop();

    public boolean isRunning();

}
