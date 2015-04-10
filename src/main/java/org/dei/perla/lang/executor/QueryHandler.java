package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.Clause;

/**
 * @author Guido Rota 10/04/15.
 */
public interface QueryHandler<E extends Clause, T> {

    public void error(E source, Throwable error);

    public void data(E source, T value);

}
