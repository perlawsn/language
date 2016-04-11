package org.dei.perla.lang.executor;

import java.util.ArrayList;

import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.query.statement.CreationStatement;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;

public class CreationManager{
	private ArrayList<CreationStatement> query = new ArrayList<CreationStatement>();
	private ArrayList<StatementHandler> handlers = new ArrayList<StatementHandler>();
	private SelectionManager sm;
	private Registry registry;
	
	public CreationManager(Registry registry) {
		this.registry=registry;
		
	}

	public StatementTask insertQuery(CreationStatement s, StatementHandler sh) {
		sm = new SelectionManager(registry);
		query.add(s);
		handlers.add(sh);
		
		return null;
		
	}

}
