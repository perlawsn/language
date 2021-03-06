package org.dei.perla.lang.persistence.memory;

import org.dei.perla.lang.persistence.FieldDefinition;
import org.dei.perla.lang.persistence.Stream;
import org.dei.perla.lang.persistence.StreamDefinition;
import org.dei.perla.lang.persistence.StreamException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory {@link Stream} implementation.
 *
 * @author Guido Rota 07/07/15.
 */
public class MemoryStream implements Stream {

    private final String id;
    private final List<FieldDefinition> fields;
    private final List<Object[]> records = new ArrayList<>();

    protected MemoryStream(StreamDefinition def) {
        id = def.getId();
        fields = def.getFields();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<FieldDefinition> getFields() {
        return fields;
    }

    @Override
    public synchronized void add(Object[] record) throws StreamException {
        if (record == null) {
            throw new StreamException("Cannot add null record");
        }
        if (record.length != fields.size()) {
            throw new StreamException("Record size mismatch");
        }

        records.add(record);
    }

}
