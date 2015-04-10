package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.util.Collection;

/**
 * @author Guido Rota 16/03/15.
 */
public interface Statement extends Clause {

    public Statement bind(Collection<Attribute> atts);

}
