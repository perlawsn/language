package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BufferView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class Selection implements Statement {

    private final Select select;
    private final WindowSize every;
    private final Sampling sampling;

    public Selection(Select select, WindowSize every, Sampling sampling) {
        this.select = select;
        this.every = every;
        this.sampling = sampling;
    }

    @Override
    public boolean hasErrors() {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public boolean isComplete() {
        return select.isComplete() && sampling.isComplete();
    }

    public Selection bind(Collection<Attribute> atts, Errors err) {
        List<Attribute> bound = new ArrayList<>();
        Select bselect = select.bind(atts, bound);
        Sampling bsampling = sampling.bind(atts, err);
        return new Selection(bselect, every, bsampling);
    }

    public List<Object[]> select(BufferView buffer) {
        return select.select(buffer);
    }

}
