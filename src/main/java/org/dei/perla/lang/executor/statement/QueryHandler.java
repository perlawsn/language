package org.dei.perla.lang.executor.statement;

/**
 * @author Guido Rota 10/04/15.
 */
public interface QueryHandler<E, T> {

    public void error(E source, Throwable cause);

    public void data(E source, T value);
    

}
