package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 23/02/15.
 */
public interface Expression {

    public DataType getType();

    public boolean isComplete();

    public boolean hasErrors();

    public Expression rebuild(List<Attribute> atts);

    public Object run(Object[] record, BufferView buffer);

}
