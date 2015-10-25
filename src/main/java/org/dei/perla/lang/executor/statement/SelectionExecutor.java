package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SelectionStatement;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Executor for the {@link SelectionStatement} class
 *
 * @author Guido Rota 22/04/15.
 */
public final class SelectionExecutor {

    private final Fpc fpc;
    private final SelectionStatement query;
    private final List<Attribute> selAtts;
    private final Expression where;
    private final QueryHandler<? super SelectionStatement, Object[]> handler;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Buffer buffer;
    private final SamplerManager sampMgr;

    public SelectionExecutor(
            SelectionStatement query,
            Fpc fpc,
            QueryHandler<? super SelectionStatement, Object[]> handler) {
        this.fpc = fpc;
        this.query = query;
        selAtts = query.getAttributes();
        where = query.getWhere();
        this.handler = handler;
        buffer = new ArrayBuffer(selAtts);
        sampMgr = new SamplerManager(
                query,
                fpc,
                new SamplerHandler());
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        sampMgr.start();
    }

    public void stop() {
        if (!running.compareAndSet(true, false)) {
            return;
        }
        sampMgr.stop();
    }

    public boolean isRunning() {
        return running.get();
    }


    /**
     * QueryHandler implementation for the sampler
     */
    private final class SamplerHandler implements
            QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable cause) {
            throw new RuntimeException("unimplemented");
        }

        @Override
        public void data(Sampling source, Object[] sample) {
            LogicValue valid = (LogicValue) where.run(sample, null);
            if (!valid.toBoolean()) {
                return;
            }

            buffer.add(sample);
        }

    }

}
