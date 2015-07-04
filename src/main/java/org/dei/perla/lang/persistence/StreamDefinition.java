package org.dei.perla.lang.persistence;

import java.util.List;

/**
 * @author Guido Rota 04/07/15.
 */
public final class StreamDefinition {

    private final String name;
    private final List<FieldDefinition> fields;

    public StreamDefinition(String name, List<FieldDefinition> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<FieldDefinition> getAttributes() {
        return fields;
    }

}
