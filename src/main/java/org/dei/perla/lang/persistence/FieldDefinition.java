package org.dei.perla.lang.persistence;

import org.dei.perla.core.fpc.DataType;

/**
 * Description of a {@link Stream} field
 *
 * @author Guido Rota 04/07/15.
 */
public final class FieldDefinition {

    private final String name;
    private final DataType type;
    private final Object def;

    public FieldDefinition(String name, DataType type) {
        this(name, type, null);
    }

    public FieldDefinition(String name, DataType type, Object def) {
        this.name = name;
        this.type = type;
        this.def = def;
    }

    public String getName() {
        return name;
    }

    public DataType getType() {
        return type;
    }

    public Object getDefaultValue() {
        return def;
    }

}
