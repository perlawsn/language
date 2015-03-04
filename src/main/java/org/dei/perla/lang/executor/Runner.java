package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.query.Query;

import java.util.List;

/**
 * @author Guido Rota 04/03/15.
 */
public abstract class Runner {

    private final Buffer buf;

    private final Fpc fpc;
    private final Query query;
    private final QueryHandler qh;

    public Runner(Fpc fpc, Query query, QueryHandler qh) {
        this.fpc = fpc;
        this.query = query;
        this.qh = qh;

        //TODO: estimate buffer length
        int tsIdx = timestampIndex(query.selectAttributes());
        buf = new ArrayBuffer(tsIdx, 512);
    }

    // Retrieves the column index of the timestamp attribute
    private int timestampIndex(List<Attribute> atts) {
        int i = 0;
        for (Attribute a : atts) {
            if (a == Attribute.TIMESTAMP) {
                return i;
            }
        }
        throw new IllegalArgumentException(
                "missing timestamp attribute in attribute list");
    }

    protected final void doSelect() {
        BufferView view = buf.unmodifiableView();
        query.getDataManager().select(view, qh);
        view.release();
    }

    protected void newSample() {}

}
