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
    private final int idx;

    public Field(Attribute att, int idx) {
        this.att = att;
        this.idx = idx;
    }

    public Attribute getAttribute() {
        return att;
    }

    public int getIndex() {
        return idx;
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
    public Object compute(Object[] cur, BufferView buffer) {
        return cur[idx];
    }

}
