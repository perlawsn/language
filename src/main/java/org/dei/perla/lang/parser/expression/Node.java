package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public interface Node {

    /**
     * Searches the list for the given {@link Attribute} identifier, and returns
     * the index of the first occurrence.
     *
     * @param id   {@link Attribute} identifier
     * @param atts list of {@link Attribute} objects
     * @return index of the {@link Attribute}, -1 if the list does not contain
     * any element with the given identifier
     */
    default public int attributeIndex(String id, List<Attribute> atts) {
        int i = 0;
        for (Attribute a : atts) {
            if (a.getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public DataType getType();

    public Expression build(List<Attribute> atts);

}
