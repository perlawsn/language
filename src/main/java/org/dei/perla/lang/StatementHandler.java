package org.dei.perla.lang;

import org.dei.perla.lang.executor.Record;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.query.statement.Statement;

/**
 * A handler for statements. This class is used by the {@link Executor} to
 * notify query results to interested consumers.
 *
 * @author Guido Rota 07/07/15.
 */
public interface StatementHandler extends QueryHandler<Statement, Object> {

   
    public void data(Statement s, Record r);
    
    @Override
    public void data(Statement s, Object r);
    
    public void complete(Statement s);

    @Override
    public void error(Statement s, Throwable cause);

	void data(Statement s, Object[] r);

}
