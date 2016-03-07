package org.dei.perla.lang.executor;

import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;

public class SelectionQueryTask implements QueryTask
{
	private SelectionDistributor sd;
	
	public SelectionQueryTask(SelectionStatement sel,Registry r, QueryHandler<Statement, Object[]> qh){
		sd= new SelectionDistributor("1", sel, qh, r);
		sd.start();
	}
		

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}

}
