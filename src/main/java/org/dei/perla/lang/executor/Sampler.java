package org.dei.perla.lang.executor;

/**
 * A simple marker interface for the Sampler query execution object
 *
 * @author Guido Rota 14/04/15.
 */
public interface Sampler {

    public void start() throws QueryException;

    public void stop();

    public boolean isRunning();

}
