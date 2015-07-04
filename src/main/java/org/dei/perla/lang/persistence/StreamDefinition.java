package org.dei.perla.lang.persistence;

import java.util.Collections;
import java.util.List;

/**
 * Description of a {@link Stream}
 *
 * @author Guido Rota 04/07/15.
 */
public final class StreamDefinition {

    private final String id;
    private final List<FieldDefinition> fields;

    public StreamDefinition(String id, List<FieldDefinition> fields) {
        this.id = id;
        this.fields = Collections.unmodifiableList(fields);
    }

    public String getId() {
        return id;
    }

    public List<FieldDefinition> getFields() {
        return fields;
    }

}
