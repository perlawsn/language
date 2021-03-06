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
public interface StatementHandler extends QueryHandler<Statement, Record> {


	@Override
	void data(Statement stat, Record record);

	void complete();

}
