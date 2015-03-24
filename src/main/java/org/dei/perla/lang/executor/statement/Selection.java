package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class Selection implements Statement {

    private final Select select;
    private final WindowSize every;

    public Selection(Select select, WindowSize every) {
        this.select = select;
        this.every = every;
    }

    public List<Object[]> select(BufferView buffer) {
        return select.select(buffer);
    }

    public List<Attribute> selectedAttributes() {
        throw new RuntimeException("unimplemented");
    }

}
