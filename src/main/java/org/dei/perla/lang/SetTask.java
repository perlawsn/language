package org.dei.perla.lang;

import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.executor.statement.SetManager;
import org.dei.perla.lang.query.statement.SetStatement;

public class SetTask implements StatementTask {

    private boolean running = false;
    private final SetManager setMgr;
	
    public SetTask(SetStatement set, Registry registry){
    	setMgr = new SetManager(set, registry);
    	start();
    }
    
    public void start(){
    	running = true;
    	setMgr.start();
    }
    
	@Override
	public void stop() {
		if(running){
			setMgr.stop();
			running = false;
			System.out.println("Set operation has stopped");
		}
		else
			System.out.println("SetTask has already been stopped");
	}

	@Override
	public boolean isRunning() {
		System.out.println("Set operation is running");
		return running;
	}

}
