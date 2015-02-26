package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collections;
import java.util.Set;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Constant implements Expression {

    private final DataType type;
    private final Object value;

    public Constant(DataType type, Object value) {
        // TODO: add congruency test between type and value?
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public Set<Attribute> fields() {
        return Collections.emptySet();
    }

    @Override
    public Object compute(Object[] cur, BufferView buffer) {
        return value;
    }

}
