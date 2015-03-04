package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.query.Query;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 04/03/15.
 */
public abstract class Runner {

    private final Lock lock = new ReentrantLock();

    private final Buffer buf;

    private final Fpc fpc;
    private final Query query;
    private final QueryHandler qh;

    public Runner(Fpc fpc, Query query, QueryHandler qh) {
        this.fpc = fpc;
        this.query = query;
        this.qh = qh;

        int tsIdx = timestampIndex(query.selectAttributes());
        //TODO: estimate buffer length
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
        query.getDataManager().select(view, (r) -> qh.newRecord(query, r));
        view.release();
    }

    protected void newSample() {}

    private final class EventTaskHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            //TODO: stop the runner
        }

        @Override
        public void newRecord(Task task, Record record) {
            lock.lock();
            try {
                buf.add(record);
                newSample();
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            //TODO: stop the runner, notify the exception
        }

    }

}
