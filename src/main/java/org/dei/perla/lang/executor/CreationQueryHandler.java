package org.dei.perla.lang.executor;

import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.query.statement.SelectionStatement;

public class CreationQueryHandler implements QueryHandler<SelectionStatement, Object[]>{
	private StatementHandler sh;


	public CreationQueryHandler(StatementHandler h) {
		sh = h;
	}
	
	public StatementHandler getHandler(){
		return sh;
	}
	
	@Override
	public void error(SelectionStatement source, Throwable cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void data(SelectionStatement source, Object[] value) {
		Record r= new Record(source.getAttributes(), value);
		sh.data(source,r);
	}


	public void complete() {
		sh.complete();	
	}
}	