package org.dei.perla.lang.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.query.statement.Statement;

public interface QueryManager {



	public StatementTask insertQuery(Statement s, StatementHandler sh);


}
