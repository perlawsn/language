package org.dei.perla.lang.parser;

import org.dei.perla.core.utils.Errors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Guido Rota 31/07/15.
 */
public final class ParseContext {

    private final Errors err = new Errors();

    private final Map<String, List<TypeVariable>> fieldTypes = new HashMap<>();

    public boolean setFieldType(String id, TypeVariable type) {
        List<TypeVariable> tl = fieldTypes.get(id);
        if (tl == null) {
            tl = new ArrayList<>();
            tl.add(type);
            fieldTypes.put(id, tl);
            return true;
        }

        for (TypeVariable t : tl) {
            if (!TypeVariable.merge(t, type)) {
                throw new RuntimeException("missing error management");
            }
        }
        tl.add(type);
        return true;
    }

}
