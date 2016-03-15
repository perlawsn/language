package org.dei.perla.lang;

import java.util.List;

import org.dei.perla.lang.executor.statement.SelectionExecutor;

/**
 * @author Guido Rota 07/07/15.
 */
public interface StatementTask {

	void stop();
	
	boolean isRunning();

}
