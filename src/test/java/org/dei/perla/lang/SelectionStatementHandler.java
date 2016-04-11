package org.dei.perla.lang;

import org.dei.perla.lang.executor.Record;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.query.statement.Statement;

public class SelectionStatementHandler implements QueryHandler<Statement, Record>{

    @Override
    public void data(Statement s, Record r) {
    	
    }

    public void complete(Statement s) { }

    @Override
    public void error(Statement s, Throwable cause) {  }



}
