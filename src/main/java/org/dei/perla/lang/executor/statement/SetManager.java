package org.dei.perla.lang.executor.statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.query.statement.SetStatement;

public final class SetManager {

	private static final int NEW = 0;
	private static final int RUNNING = 1;
	private static final int STOPPED = 2;
	
	private final SetStatement query;
	private final Registry registry;
	 
	private volatile int status = NEW;

	private final List<SetExecutor> execs = new ArrayList<>();
	private final Set<Fpc> managed = new HashSet<>();

	public SetManager(SetStatement query, Registry registry){
		 this.query = query;
		 this.registry = registry;
		 start();
	}
	 
	public synchronized void start() {
	        if (status != NEW) {
	            throw new IllegalStateException("Cannot start, " +
	        "SetManager has already been started");
	    }
	    distribute();
	    status = RUNNING;
	}
	
	private void distribute() {
	    for(Integer id: query.getIds()){
	    	Fpc fpc = registry.get(id);
	    	if(fpc != null)
	    		managed.add(fpc);	
	    	else 
	    		//TODO cambiare in eccezione?
	    		System.out.println("Non esiste un FPC con id " + id);
	    }
	    for (Fpc fpc : managed) {
	    	SetExecutor se = new SetExecutor(query, fpc);
            execs.add(se);
            se.start();
        }
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

	
	 
}
