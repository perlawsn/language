package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.Selection;

/**
 * @author Guido Rota 03/03/15.
 */
public interface QueryHandler {

    public void newRecord(Selection q, Object[] r);

    public void error(Selection q, Throwable cause);

}
