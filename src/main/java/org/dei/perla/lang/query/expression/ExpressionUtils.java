package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.Attribute;

import java.util.Collection;

/**
 * @author Guido Rota 04/07/15.
 */
public final class ExpressionUtils {

    /**
     * Searches the {@link Attribute} with the specified id in the {@link
     * Collection} passed as parameter.
     *
     * @param id {@link Attribute} identifier
     * @param atts {@link Collection} of {@link Attribute}s
     * @return {@link Attribute} with the specified id, null if not found
     */
    public static Attribute getById(String id, Collection<Attribute> atts) {
        for (Attribute a : atts) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

}
