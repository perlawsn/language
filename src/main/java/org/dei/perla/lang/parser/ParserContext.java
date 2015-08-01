package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.parser.ast.AttributeReferenceAST;

import java.util.*;
import java.util.Map.Entry;

/**
 * Context object for storing intermediate parsing results.
 *
 * @author Guido Rota 31/07/15.
 */
public final class ParserContext {

    private final Errors err = new Errors();

    private final Map<String, List<AttributeReferenceAST>> atts = new HashMap<>();

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
    public boolean addAttributeReference(AttributeReferenceAST att) {
        String id = att.getIdentifier();
        List<AttributeReferenceAST> usages = atts.get(id);
        if (usages == null) {
            usages = new ArrayList<>();
            usages.add(att);
            atts.put(id, usages);
            return true;
        }

        boolean clean = true;
        TypeVariable curr = att.getType();
        for (AttributeReferenceAST t : usages) {
            TypeVariable prev = t.getType();
            if (!TypeVariable.merge(curr, prev)) {
                String msg = "Incompatible type for attribute '" + id + "': " +
                        "usage at " + att.getPosition() + " of type '" +
                        curr + "' is not compatible with previous usage of " +
                        "type '" + prev + "'.";
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
        for (Entry<String, List<AttributeReferenceAST>> e : atts.entrySet()) {
            String id = e.getKey();
            List<AttributeReferenceAST> fl = e.getValue();
            // All the FieldAST inside the field's list are guaranteed
            // to be of the same TypeClass if no type errors were found.
            // Hence, we can just retrieve the first one.
            fm.put(id, fl.get(0).getType().getTypeClass());
        }
        return fm;
    }

}
