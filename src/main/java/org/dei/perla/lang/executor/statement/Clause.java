package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.util.List;
import java.util.Set;

/**
 * @author Guido Rota 23/03/15.
 */
public interface Clause {

    public boolean hasErrors();

    public boolean isComplete();

    public Set<String> getFields();

    public Clause bind(List<Attribute> atts);

}
