package org.dei.perla.lang.executor;

/**
 * @author Guido Rota 13/04/15.
 */
public class NoopQueryHandler<E, T>
        implements QueryHandler<E, T> {

    @Override
    public void error(E source, Throwable error) { }

    @Override
    public void data(E source, T value) { }

}
