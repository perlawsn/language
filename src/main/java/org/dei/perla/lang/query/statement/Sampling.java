package org.dei.perla.lang.query.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;

import java.util.Collection;

/**
 * @author Guido Rota 30/03/15.
 */
public interface Sampling {

    public Sampling bind(Collection<Attribute> atts, Errors err);

    public boolean isComplete();

}
