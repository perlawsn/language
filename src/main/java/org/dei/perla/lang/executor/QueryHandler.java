package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.query.Query;

/**
 * @author Guido Rota 03/03/15.
 */
public interface QueryHandler {

    public void newRecord(Query q, Object[] r);

    public void error(Query q, Throwable cause);

}
