package org.dei.perla.lang.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guido Rota 31/07/15.
 */
public final class ParseContext {

    private final Map<String, TypeVariable> fieldTypes = new HashMap<>();

    private void setFieldType(String id, TypeVariable type) {
        fieldTypes.put(id, type);
    }

    private TypeVariable getFieldType(String id) {
        return fieldTypes.get(id);
    }

}
