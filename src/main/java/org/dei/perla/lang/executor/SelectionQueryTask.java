package org.dei.perla.lang.executor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.executor.statement.SelectionManager;
import org.dei.perla.lang.executor.statement.SelectionManager.SelectionQueryHandler;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;

public class SelectionQueryTask implements QueryTask
{
	private SelectionDistributor sd;
	private SelectionManager sm;
	private final Lock lk = new ReentrantLock();
	
	public SelectionQueryTask(SelectionStatement sel,Registry r, StatementHandler handler){
		sm= new SelectionManager(sel, handler);
		sd= new SelectionDistributor("1", sel, (QueryHandler) sm.getHandler(), r);
		sd.start();
	}
		

	@Override
	public boolean isRunning() {
		return sd.isRunning() ;
	}

	@Override
	public void stop() {
		lk.lock();
        try {
        	if(sd.isRunning()){
        		sd.stop();
        	}else{
        		System.out.println("Selection Query Task already stopped");
        	}
        } finally {
            lk.unlock();
        }
	}

}
