package org.dei.perla.lang.executor.query;

import org.dei.perla.core.record.Attribute;

import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public final class Query {

    private final DataManager dm;

    public Query(DataManager dm) {
        this.dm = dm;
    }

    public DataManager getDataManager() {
        return dm;
    }

    public List<Attribute> selectAttributes() {
        throw new RuntimeException("unimplemented");
    }

}
