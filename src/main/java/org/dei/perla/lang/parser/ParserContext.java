package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.parser.ast.AttributeAST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Context object for storing intermediate parsing results.
 *
 * @author Guido Rota 31/07/15.
 */
public final class ParserContext {

    private final Errors err = new Errors();

    private final Map<String, List<AttributeAST>> atts = new HashMap<>();

    public void addError(String msg) {
        err.addError(msg);
    }

    /**
     * Returns the number of errors encountered while parsing.
     *
     * @return number of errors encountered while parsing
     */
    public int getErrorCount() {
        return err.getErrorCount();
    }

    /**
     * Adds a new usage of a
     * @param att
     * @return
     */
    public boolean addAttributeReference(AttributeAST att) {
        String id = att.getId();
        List<AttributeAST> usages = atts.get(id);
        if (usages == null) {
            usages = new ArrayList<>();
            usages.add(att);
            atts.put(id, usages);
            return true;
        }

        boolean clean = true;
        for (AttributeAST ot : usages) {
            if (!att.mergeTypes(ot)) {
                String msg = "Incompatible type for attribute '" + id + "': " +
                        "usage at " + att.getPosition() + " of type '" +
                        att.getTypeClass() + "' is not compatible with " +
                        "previous usage of type '" + ot.getTypeClass()  + "' " +
                        "at " + ot.getPosition() + ".";
                err.addError(msg);
                clean = false;
            }
        }

        if (clean) {
            usages.add(att);
        }

        return clean;
    }

    public Map<String, TypeClass> getAttributeTypes() {
        Map<String, TypeClass> fm = new HashMap<>();
        for (Entry<String, List<AttributeAST>> e : atts.entrySet()) {
            String id = e.getKey();
            List<AttributeAST> fl = e.getValue();
            // All the FieldAST inside the field's list are guaranteed
            // to be of the same TypeClass if no type errors were found.
            // Hence, we can just retrieve the first one.
            fm.put(id, fl.get(0).getTypeClass());
        }
        return fm;
    }

}
