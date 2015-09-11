package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Attribute;

import java.util.List;

/**
 * @author Guido Rota 03/07/15.
 */
public final class Record {

    private final List<Attribute> fields;
    private final Object[] values;

    public Record(List<Attribute> fields, Object[] values) {
        this.fields = fields;
        this.values = values;
    }

    public List<Attribute> getFields() {
        return fields;
    }

    public Object[] getValues() {
        return values;
    }

}
