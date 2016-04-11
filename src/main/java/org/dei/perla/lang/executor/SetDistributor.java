package org.dei.perla.lang.executor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Sample;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.executor.statement.SetExecutor;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.SetStatement;

public class SetDistributor {
	  private static final int NEW = 0;
	    private static final int RUNNING = 1;
	    private static final int STOPPED = 2;


	    private final SetStatement query;
	    private final List<Integer> ec;
	    private final QueryHandler handler;
	    private final Registry registry;

	    private volatile int status = NEW;

	    private final List<SetExecutor> execs = new ArrayList<>();
	    private final List<SetHandler> fpcHandler= new ArrayList<>();
	    private final List<Task> tasks= new ArrayList<>();
	    private final Set<Fpc> managed = new HashSet<>();

	    protected SetDistributor(SetStatement query,
           QueryHandler handler,
	            Registry registry) {
	        this.query = query;
	        ec =  query.getIds();
	        this.handler = handler;
	        this.registry = registry;
	    }

	    public synchronized void start() {
	        if (status != NEW) {
	            throw new IllegalStateException("Cannot start, " +
	                    "SelectionDistributor has already been started");
	        }

	        distribute();
	        status = RUNNING;
	    }
	    

	    private void distribute() {
	        Collection<Fpc> fpcs;
	        fpcs = registry.getAll();
	        if (ec.isEmpty()) {
	        	throw new IllegalStateException(
                        "Set Distributor, set query on empty set of fpcs");
	        } else {
	        	for(int i:ec)
	            fpcs.add(registry.get(i));
	        }
	        if(fpcs.isEmpty())
	        	throw new IllegalStateException(
                        "Set Distributor, no fpcs found for the specified ids");
	        for (Fpc fpc : fpcs) {
	            if (managed.contains(fpc)) {
	                continue;
	            }
	            SetHandler h = new SetHandler();
	            fpcHandler.add(h);
	            SetExecutor se = new SetExecutor(query, fpc, h);
	            execs.add(se);
	            managed.add(fpc);
	            Task t=se.start();
	            tasks.add(t);
	        }
	    }
	    
	    private void checkComplention() {
			//if(tasks.isEmpty())
				//handler.complete();			
		}

	    public synchronized void stop() {
	        if (status == STOPPED) {
	            return;
	        }

	        execs.forEach(SetExecutor::stop);
	        status = STOPPED;
	    }

	    public synchronized boolean isRunning() {
	        return status == RUNNING;
	    }
	    
		private class SetHandler implements TaskHandler {
			@Override
			public void complete(Task task) {
					tasks.remove(task);
					checkComplention();
			    }

			@Override
	        public void data(Task task, Sample sample) {
		         return;
	        }


	        @Override
	        public void error(Task task, Throwable cause) {
	        	
	        }
		}
}
