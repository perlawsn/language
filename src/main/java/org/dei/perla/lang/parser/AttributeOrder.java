package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;

import java.util.*;

/**
 * A utility class employed to associate expression {@link Attribute}
 * references with a unique integer index. It allows to create an ordered
 * list of {@link Attribute}s that can be used when querying the {@link Fpc}
 * for samples.
 *
 * @author Guido Rota 24/09/15.
 */
public final class AttributeOrder {

    private final Map<String, Integer> order = new HashMap<>();

    /**
     * Retrieves the index associated with the {@link Attribute} passed as
     * parameter
     *
     * @param id {@link Attribute} identifier
     * @return {@link Attribute} index
     */
    public int getIndex(String id) {
        Integer idx = order.get(id);
        if (idx == null) {
            idx = order.size();
            order.put(id, idx);
        }

        return idx;
    }

    /**
     * Returns a list of {@link Attribute}s, ordered by index
     * @param ctx
     * @return
     */
    public List<Attribute> toList(ParserContext ctx) {
        Attribute[] as = new Attribute[order.size()];
        Map<String, DataType> types = ctx.getAttributeTypes();

        for (Map.Entry<String, Integer> e : order.entrySet()) {
            String id = e.getKey();
            int idx = e.getValue();
            DataType type = types.get(id);
            if (type == null) {
                throw new RuntimeException(
                        "Compiler bug: missing attribute type");
            }
            as[idx] = Attribute.create(id, type);
        }

        return Collections.unmodifiableList(Arrays.asList(as));
    }

}
