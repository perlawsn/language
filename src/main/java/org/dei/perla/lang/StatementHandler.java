package org.dei.perla.lang;

import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.query.statement.Statement;

/**
 * A handler for statements. This class is used by the {@link Executor} to
 * notify query results to interested consumers.
 *
 * @author Guido Rota 07/07/15.
 */
public interface StatementHandler<S extends Statement> extends
        QueryHandler<S, Record> {

    @Override
    public void data(S statement, Record record);

    public void complete(S statement);

    @Override
    public void error(S statement, Throwable cause);

}
