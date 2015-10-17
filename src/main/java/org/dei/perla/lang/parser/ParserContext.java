package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
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
     * Indicates if one or more errors have been added to this context object
     * while parsing
     *
     * @return true if one or more errors were found during the parsing
     * phase, false otherwise
     */
    public boolean hasErrors() {
        return !err.isEmpty();
    }

    /**
     * Returns a textual description of all errors encountered while parsing.
     *
     * @return Description of all errors encountered during parsing
     */
    public String getErrorDescription() {
        return err.asString();
    }

    /**
     * Tracks a new usage of an attribute. This function checks that the type
     * of all previous usages is compatible with the new one
     *
     * @param att
     * @return
     */
    public boolean addAttributeReference(AttributeReferenceAST att) {
        String id = att.getId();
        List<AttributeReferenceAST> usages = atts.get(id);
        if (usages == null) {
            usages = new ArrayList<>();
            usages.add(att);
            atts.put(id, usages);
            return true;
        }

        boolean clean = true;
        for (AttributeReferenceAST ot : usages) {
            if (!att.mergeTypes(ot)) {
                String msg = "Incompatible type for attribute '" + id + "': " +
                        "usage at " + att.getPosition() + " of type '" +
                        att.getType() + "' is not compatible with " +
                        "previous usage of type '" + ot.getType()  + "' " +
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

    public Map<String, DataType> getAttributeTypes() {
        Map<String, DataType> fm = new HashMap<>();
        for (Entry<String, List<AttributeReferenceAST>> e : atts.entrySet()) {
            String id = e.getKey();
            List<AttributeReferenceAST> fl = e.getValue();
            // All the FieldAST inside the field's list are guaranteed
            // to be of the same DataType if no type errors were found.
            // Hence, we can just retrieve the first one.
            fm.put(id, fl.get(0).getType());
        }
        return Collections.unmodifiableMap(fm);
    }

    public Set<Attribute> getAttributes() {
        Set<Attribute> s = new HashSet<>();
        for (Entry<String, List<AttributeReferenceAST>> e : atts.entrySet()) {
            String n = e.getKey();
            // All the FieldAST inside the field's list are guaranteed
            // to be of the same DataType if no type errors were found.
            // Hence, we can just retrieve the first one.
            DataType t = e.getValue().get(0).getType();
            s.add(Attribute.create(n, t));
        }
        return Collections.unmodifiableSet(s);
    }

}
