package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.QueryHandler;

/**
 * @author Guido Rota 13/04/15.
 */
public class NoopQueryHandler
        implements QueryHandler<Object, Object[]> {

    @Override
    public void error(Object source, Throwable error) { }

    @Override
    public void data(Object source, Object[] value) { }

}
