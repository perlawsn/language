package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.ClauseHandler;

/**
 * @author Guido Rota 13/04/15.
 */
public class NoopClauseHandler
        implements ClauseHandler<Object, Object[]> {

    @Override
    public void error(Object source, Throwable error) { }

    @Override
    public void data(Object source, Object[] value) { }

}
