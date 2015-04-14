package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.sample.Attribute;

import java.util.Collection;

/**
 * @author Guido Rota 30/03/15.
 */
public interface Sampling extends Clause {

    public Sampling bind(Collection<Attribute> atts);

}
