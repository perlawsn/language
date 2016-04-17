package org.dei.perla.lang.query.statement;

import java.util.Collections;
import java.util.List;

/**
 * Group by clause
 *
 * @author Guido Rota 04/03/15.
 */
public final class GroupBy {

    // Shorthand for an empty group by clause
    public static final GroupBy NONE = new GroupBy();

    private final List<String> fields;

    // Private constructor, only employed to create the NONE static reference
    private GroupBy() {
        fields = null;
    }

    public GroupBy(List<String> groups) {
        this.fields = Collections.unmodifiableList(groups);
    }

    public List<String> getFields() {
        if (this == NONE) {
            throw new IllegalStateException("No groups specified");
        }
        return fields;
    }

    public String toString(){
    	if(fields == null)
    		return "GROUP BY NONE";
    	else 
    		return "GROUP BY " + fields;
    }
}
