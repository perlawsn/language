package org.dei.perla.lang;

import java.util.List;

import org.dei.perla.lang.executor.QueryTask;
import org.dei.perla.lang.executor.SelectionQueryTask;
import org.dei.perla.lang.executor.statement.SelectionExecutor;

public class SelectionStatementTask implements StatementTask {

	private QueryTask qt;
	
	public SelectionStatementTask(SelectionQueryTask qt){
		this.qt=qt;
	}
	public boolean isRunning() {
		return qt.isRunning();
	}
	
	public void stop(){
		qt.stop();
	}

}
