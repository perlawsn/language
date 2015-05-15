package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.executor.statement.SelectionExecutor;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.SelectionQuery;

import java.util.*;

/**
 * @author Guido Rota 30/04/15.
 */
public class SelectionDistributor {

    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int STOPPED = 2;

    private final SelectionQuery query;
    private final ExecutionConditions ec;
    private final QueryHandler<? super SelectionQuery, Object[]> handler;
    private final Registry registry;

    private int status = READY;

    private final Map<Fpc, SelectionExecutor> active = new HashMap<>();
    private final List<Fpc> ignore = new ArrayList<>();

    protected SelectionDistributor(SelectionQuery query,
            QueryHandler<? super SelectionQuery, Object[]> handler,
            Registry registry) {
        this.query = query;
        ec = query.getExecutionConditions();
        this.handler = handler;
        this.registry = registry;
    }

    public synchronized void start() {
        if (status != READY) {
            throw new IllegalStateException("Cannot start, " +
                    "SelectionDistributor has already been started");
        }

        distribute();
        status = RUNNING;
    }

    private void distribute() {
        Collection<Fpc> available;

        if (ec.getSpecs().isEmpty()) {
            available = registry.getAll();
        } else {
            available = registry.get(ec.getSpecs(),
                    Collections.emptyList());
        }

        for (Fpc fpc : available) {
            if (active.containsKey(fpc)) {
                continue;
            }

            SelectionExecutor se = new SelectionExecutor(query, handler, fpc);
            active.put(fpc, se);
            se.start();
        }
    }

    public synchronized void stop() {
        if (status == STOPPED) {
            return;
        }

        active.forEach((f, e) -> e.stop());
        status = STOPPED;
    }

    public synchronized boolean isRunning() {
        return status == RUNNING;
    }


    /**
     * Handler for the EXECUTE IF refresh condition. When triggered, the
     * registry is queried to check if the query can be started on new FPCs
     *
     * @author Guido Rota 29/04/2013
     */
    private class RefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void complete(Refresh source) { }

        @Override
        public void error(Refresh source, Throwable cause) {
            synchronized (SelectionDistributor.this) {
                distribute();
            }
        }

        @Override
        public void data(Refresh source, Void value) {
            synchronized (SelectionDistributor.this) {
                distribute();
            }
        }

    }

}
