package org.dei.perla.lang.simfpc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guido Rota 13/04/15.
 */
public class FpcAction {

    private final Map<String, Object> fields = new HashMap<>();

    protected void addField(String name, Object value) {
        name = name.toLowerCase();
        if (fields.containsKey(name)) {
            throw new IllegalArgumentException("Field '" + name + "' has " +
                    "already been defined");
        }
        fields.put(name.toLowerCase(), value);
    }

    public Object getField(String name) {
        return fields.get(name);
    }

}
