package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;

import java.util.*;

/**
 * @author Guido Rota 23/03/15.
 */
public interface Clause {

    public boolean hasErrors();

    public boolean isComplete();

}
