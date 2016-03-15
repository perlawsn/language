package org.dei.perla.lang.executor.statement;

import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.executor.Record;
import org.dei.perla.lang.query.expression.Aggregate;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.SelectionStatement;


public final class SelectionManager {
	SelectionStatement query;
	StatementHandler handler;
	SelectionQueryHandler sqHandler;
	
	
	public SelectionManager(SelectionStatement query,StatementHandler handler){
		this.query = query;
		this.handler = handler;
		this.sqHandler = new SelectionQueryHandler();
	}
	
	public void compute(SelectionStatement source, Object[] value){
		Record rec=new Record(source.getAttributes(), value);		
		for(Expression e:source.getSelect().getFields()){
			if(e instanceof Aggregate){
				computeAggregate(source,value);
			}
		}
		this.handler.data(source, rec);
		
	}
	
	private void computeAggregate(SelectionStatement source, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	public SelectionQueryHandler getHandler() {
		return this.sqHandler;
	}

	public void complete(){
		
	}
	
		public class SelectionQueryHandler implements QueryHandler<SelectionStatement, Object[] >{

		@Override
		public void error(SelectionStatement source, Throwable cause) {
			handler.error(source, cause);
		}

		@Override
		public void data(SelectionStatement source, Object[] value) {
			compute(source,value);			
		}

	}



}
