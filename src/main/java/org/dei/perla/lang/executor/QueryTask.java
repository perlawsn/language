package org.dei.perla.lang.executor;

/**
 * @author Guido Rota 07/11/15.
 */
public interface QueryTask {

    public boolean isRunning();

    public void stop();

}
