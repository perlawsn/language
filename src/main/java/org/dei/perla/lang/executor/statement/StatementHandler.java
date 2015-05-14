package org.dei.perla.lang.executor.statement;

import org.dei.perla.lang.query.statement.Statement;

/**
 * @author Guido Rota 14/05/15.
 */
public interface StatementHandler<E extends Statement> {

    public void complete(E statement);

    public void data(E statement, Object[] record);

    public void error(E statement, Throwable cause);

}
