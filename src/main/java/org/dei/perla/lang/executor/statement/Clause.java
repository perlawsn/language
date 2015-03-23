package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.utils.Errors;

import java.util.List;

/**
 * @author Guido Rota 23/03/15.
 */
public interface Clause {

    public boolean hasErrors();

    public boolean isComplete();

    public Clause bind(List<Attribute> atts);

}
