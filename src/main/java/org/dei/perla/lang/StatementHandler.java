package org.dei.perla.lang;

import org.dei.perla.lang.executor.Record;
import org.dei.perla.lang.query.statement.Statement;

/**
 * @author Guido Rota 07/07/15.
 */
public interface StatementHandler {

    public void data(Statement s, Record r);

    public void complete(Statement s);

    public void error(Statement s, Throwable cause);

}
