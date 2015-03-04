package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.query.Query;
import org.dei.perla.lang.executor.query.SelectHandler;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 04/03/15.
 */
public abstract class Runner {

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private final AtomicBoolean inSelect = new AtomicBoolean(false);
    private final Lock lk = new ReentrantLock();

    private final Buffer buf;

    private final Fpc fpc;
    private final Query query;
    private final QueryHandler qh;

    private Task sampTask;
    private final SelectHandler selHandler = new RunnerSelectHandler();
    private final TaskHandler sampHandler = new SamplingTaskHandler();

    public Runner(Fpc fpc, Query query, QueryHandler qh) {
        this.fpc = fpc;
        this.query = query;
        this.qh = qh;

        int tsIdx = timestampIndex(query.selectAttributes());
        // TODO: estimate buffer length
        buf = new ArrayBuffer(tsIdx, 512);

        // TODO: correct termination, executeif and sampling management
        sampTask = fpc.get(query.selectAttributes(), 1000, sampHandler);
    }

    // Retrieves the index of the timestamp column
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
        if (!inSelect.compareAndSet(false, true)) {
            // TODO: cannot keep up with the selects
            // Stop the query
            throw new RuntimeException("unimplemented");
        }
        pool.submit(() -> {
            BufferView view = buf.unmodifiableView();
            query.getDataManager().select(view, selHandler);
            view.release();
            // TODO: delete old records from buffer
            inSelect.set(false);
        });
    }

    protected void newSample() {}

    private final class RunnerSelectHandler implements SelectHandler {

        @Override
        public void newRecord(Object[] r) {
            qh.newRecord(query, r);
            // TODO: manage query termination
        }

    }

    private final class SamplingTaskHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            // TODO: stop the runner
        }

        @Override
        public void newRecord(Task task, Record record) {
            lk.lock();
            try {
                buf.add(record);
                newSample();
            } finally {
                lk.unlock();
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: stop the runner, notify the exception
        }

    }

}
