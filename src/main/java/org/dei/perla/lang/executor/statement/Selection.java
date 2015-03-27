package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Collection;
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

    @Override
    public boolean hasErrors() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public boolean isComplete() {
        throw new RuntimeException("unimplemented");
    }

    public Selection bind(Collection<Attribute> atts, List<Attribute> bound) {
        throw new RuntimeException("unimplemented");
    }

    public List<Object[]> select(BufferView buffer) {
        return select.select(buffer);
    }

}
