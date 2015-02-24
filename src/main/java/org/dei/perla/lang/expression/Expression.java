package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Set;

/**
 * @author Guido Rota 23/02/15.
 */
public interface Expression {

    public DataType getType();

    public Set<Attribute> fields();

    public Object compute(int index, BufferView buffer);

}
