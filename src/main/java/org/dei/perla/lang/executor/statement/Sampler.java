package org.dei.perla.lang.executor.statement;

/**
 * A simple marker interface for the Sampler executor
 *
 * @author Guido Rota 14/04/15.
 */
public interface Sampler {

    /**
     * Starts the execution of the {@link Sampling} clause. Startup errors
     * will be asynchronously notified through the {@link QueryHandler}
     * specified in the constructor after the {@code start()} method is
     * over.
     */
    public void start();

    /**
     * Stops executing the {@link Sampling} clause. The clause can be resumed
     * by invoking the {@code start()} method anew.
     */
    public void stop();

    /**
     * Indicates if the {@link Sampling} clause executor is running or not
     *
     * @return true if the executor is running, false otherwise
     */
    public boolean isRunning();

}
