package org.dei.perla.lang.executor.statement;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Sample;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.lang.query.statement.SetStatement;

public final class SetExecutor {
	
	private static final int READY = 0;
	private static final int RUNNING = 1;
	private static final int STOPPED = 2;
	private static final int ERROR = 3;
	private final Fpc fpc;
	private final SetStatement set;

	private final Lock lk = new ReentrantLock();
    private int status = READY;
	
    Task task;
    private TaskHandler setHandler;
    
	
	public SetExecutor(SetStatement query, Fpc fpc, TaskHandler handler) {
		this.set = query;
		this.fpc = fpc;
		this.setHandler = handler;
	}

	public Task start() {
        lk.lock();
        try {
            if (status == RUNNING) {
                return task;
            } else if (status == STOPPED) {
                throw new IllegalStateException(
                        "Cannot restart SelectionExecutor");
            }
            status = RUNNING;
            Map<Attribute, Object> values = new HashMap<>(set.getParameters().size());
            set.getParameters().forEach((p -> values.put(p.getAttribute(), p.getValue())));
            task = fpc.set(values, true, setHandler);
            if (task == null) {
                status = ERROR;
                System.out.println(setErrorString(values)); //TODO si dovrebbe aggiungere in un log
            }
        } finally {
            lk.unlock();
        }
        return task;
    }
	

	    private String setErrorString(Map<Attribute, Object>  values) {
	        StringBuilder bld =
	                new StringBuilder("Error starting SET QUERY: ");
	        bld.append("cannot set attributes ");
	        values.forEach((k,v) -> bld.append(v).append(" = ").append(v).append(", "));
	        bld.append("from FPC ").append(fpc.getId());
	        return bld.toString();
	    }

	  public void stop() {
	        lk.lock();
	        try {
	            if (status == STOPPED) {
	                return;
	            } else if (status == READY) {
	                throw new IllegalStateException(
	                        "SelectionExecutor has not been started");
	            }
	            status = STOPPED;
	            task.stop();
	        } finally {
	            lk.unlock();
	        }
	    }

	    public boolean isRunning() {
	        lk.lock();
	        try {
	            return status == RUNNING;
	        } finally {
	            lk.unlock();
	        }
	    }
	
}
