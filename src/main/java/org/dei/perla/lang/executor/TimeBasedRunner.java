package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.statement.Selection;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Guido Rota 04/03/15.
 */
public final class TimeBasedRunner {

    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private final AtomicBoolean inSelect = new AtomicBoolean(false);

    private final Buffer buf;

    private final Fpc fpc;
    private final Selection query;
    private final QueryHandler qh;

    private Task sampTask;
    private final TaskHandler sampHandler = new SamplingTaskHandler();

    private int samples;

    public TimeBasedRunner(Fpc fpc, Selection query, QueryHandler qh) {
        this.fpc = fpc;
        this.query = query;
        this.qh = qh;

        buf = null;
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
            query.select(view);
            view.release();
            // TODO: delete old records from buffer
            inSelect.set(false);
        });
    }

    private final class SamplingTaskHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            // TODO: start a new sampling task if the frequency is changing,
            // stop and notify an exception otherwise.
        }

        @Override
        public void newRecord(Task task, Record record) {
            buf.add(record);
        }

        @Override
        public void error(Task task, Throwable cause) {
            // TODO: stop the runner, notify the exception
        }

    }

}
