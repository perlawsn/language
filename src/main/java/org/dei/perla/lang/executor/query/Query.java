package org.dei.perla.lang.executor.query;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class Query {

    private final Selection selection;

    public Query(Selection selection) {
        this.selection = selection;
    }

    public void select(BufferView buffer, SelectHandler handler) {
        selection.select(buffer, handler);
    }

    public List<Attribute> selectedAttributes() {
        throw new RuntimeException("unimplemented");
    }

}
