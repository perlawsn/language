package org.dei.perla.lang.executor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.executor.SelectionManager.SelectionStatementTask;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.executor.statement.SetExecutor;
import org.dei.perla.lang.query.statement.SetStatement;

public final class SetManager {

	private ArrayList<SetStatement> query = new ArrayList<SetStatement>();
	private ArrayList<StatementHandler> handlers = new ArrayList<StatementHandler>();
	private ArrayList<SetDistributor> distributors = new ArrayList<SetDistributor>();
	private ArrayList<SetStatementTask> stasks = new ArrayList<SetStatementTask>();
	private Registry registry;
	
	private ArrayList<SetQueryTask> qtasks = new ArrayList<SetQueryTask>();
	private ArrayList<SetQueryHandler> qhandler = new ArrayList<SetQueryHandler>();
	
	
	
	public SetManager(Registry registry) {
		this.registry=registry;
	}
	
	public StatementTask insertQuery(SetStatement set, StatementHandler h) {
		query.add(set);
		handlers.add(h);
		SetStatementTask sst = new SetStatementTask();
		stasks.add(sst);
		SetQueryHandler sqh = new SetQueryHandler();
		qhandler.add(sqh);
		SetQueryTask sqt = new SetQueryTask();
		qtasks.add(sqt);
		SetDistributor sd = new SetDistributor(set, sqh, registry);
		distributors.add(sd);
		if(checkConcurrency(sst))
			sd.start();
		return sst;
	}

	private boolean checkConcurrency(SetStatementTask sst) {
				return true;
	}

	private void startWaitingQuery(){
		
	}
	
	private void clean(int i){
		distributors.remove(i);
		qtasks.remove(i);
		qhandler.remove(i);
		handlers.remove(i);
		stasks.remove(i);
	}

		public class SetStatementTask implements StatementTask{

			@Override
			public synchronized void stop() {
				int i = stasks.indexOf(this);
				 qtasks.get(i).stop();				 
			}

			@Override
			public boolean isRunning() {
				int i = stasks.indexOf(this);
				return qtasks.get(i).isRunning();
			}
			

		}
		
		public class SetQueryTask implements QueryTask{

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
		
		public class SetQueryHandler implements QueryHandler{

			@Override
			public void error(Object source, Throwable cause) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void data(Object source, Object value) {
				// TODO Auto-generated method stub
				
			}


			public void complete() {
				int i= qhandler.indexOf(this);
				handlers.get(i).complete();	
				clean(i);
				startWaitingQuery();
			}
			
		}


	 
}
