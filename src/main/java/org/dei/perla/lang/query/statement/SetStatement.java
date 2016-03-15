package org.dei.perla.lang.query.statement;

import java.util.Collections;
import java.util.List;


/**
 * @author Guido Rota 07/07/15.
 */
public final class SetStatement implements Statement {
	
	private final List<SetParameter> params;
	private final List<Integer> ids;

	public SetStatement(List<SetParameter> params, List<Integer> ids) {
	    this.params = Collections.unmodifiableList(params);
	    this.ids = Collections.unmodifiableList(ids);
	}
	
	public List<SetParameter> getParameters() {
	    return params;
	}
	
	public List<Integer> getIds() {
	    return ids;
	}
}
