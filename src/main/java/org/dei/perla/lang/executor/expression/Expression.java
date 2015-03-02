package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;

/**
 * @author Guido Rota 23/02/15.
 */
public interface Expression {

    public DataType getType();

    public Object compute(Object[] record, BufferView buffer);

}
