package org.dei.perla.lang.executor;

import java.util.ArrayList;

import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.executor.SelectionManager.SelectionQueryTask;
import org.dei.perla.lang.executor.SelectionManager.SelectionStatementTask;
import org.dei.perla.lang.query.statement.CreationStatement;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;

public class CreationManager{
	private ArrayList<CreationStatement> query = new ArrayList<CreationStatement>();
	private ArrayList<StatementHandler> handlers = new ArrayList<StatementHandler>();
	private SelectionManager sm;
	private Registry registry;
	private ArrayList<CreationStatementTask> stasks = new ArrayList<CreationStatementTask>();
	private ArrayList<CreationQueryTask> qtasks = new ArrayList<CreationQueryTask>();
	private ArrayList<CreationQueryHandler> qhandler = new ArrayList<CreationQueryHandler>();
	public CreationManager(Registry registry) {
		this.registry=registry;
		sm = new SelectionManager(registry);
		
	}

	public StatementTask insertQuery(CreationStatement s, StatementHandler sh) {
		query.add(s);
		handlers.add(sh);
		
		return null;
		
	}
	
	private void clean(int i){
		distributors.remove(i);
		qtasks.remove(i);
		qhandler.remove(i);
		handlers.remove(i);
		stasks.remove(i);
	}
	
	public class CreationStatementTask implements StatementTask{

		@Override
		public void stop() {
			int i = stasks.indexOf(this);
			 qtasks.get(i).stop();		
		}

		@Override
		public boolean isRunning() {
			int i = stasks.indexOf(this);
			return qtasks.get(i).isRunning();
		}
		

	}
	
	
	public class CreationQueryTask implements QueryTask{

		@Override
		public boolean isRunning() {
			int i = qtasks.indexOf(this);
			return distributors.get(i).isRunning();
		}

		@Override
		public void stop() {
			int i = qtasks.indexOf(this);
			distributors.get(i).stop();
			clean(i);
			
		}
		
	}
}
