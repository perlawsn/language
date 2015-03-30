package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 30/03/15.
 */
public interface Sampling extends Clause {

    public Sampling bind(Collection<Attribute> atts, List<Attribute> bound);

}
