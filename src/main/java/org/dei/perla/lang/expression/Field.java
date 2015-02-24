package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Field implements Expression {

    private final Attribute att;

    // cached index
    private int idx = -1;

    public Field(Attribute att) {
        this.att = att;
    }

    public Attribute getAttribute() {
        return att;
    }

    @Override
    public DataType getType() {
        return att.getType();
    }

    @Override
    public Set<Attribute> fields() {
        Set<Attribute> s = new TreeSet<>();
        s.add(att);
        return s;
    }

    @Override
    public Object compute(int index, BufferView buffer) {

        return null;
    }

}
