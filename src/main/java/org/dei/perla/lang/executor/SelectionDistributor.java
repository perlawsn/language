package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.executor.statement.SelectionExecutor;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.SelectionQuery;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 30/04/15.
 */
public class SelectionDistributor {

    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int STOPPED = 2;

    private final SelectionQuery query;
    private final QueryHandler<?super SelectionQuery, Object[]> handler;
    private final Registry registry;

    private int status = READY;

    private final Map<Fpc, SelectionExecutor> active = new HashMap<>();
    private final List<Fpc> ignore = new ArrayList<>();

    protected SelectionDistributor(SelectionQuery query,
            QueryHandler<? super SelectionQuery, Object[]> handler,
            Registry registry) {
        this.registry = registry;
        this.handler = handler;
        this.query = query;
    }

    public synchronized void start() {
        if (status != READY) {
            throw new IllegalStateException("Cannot start, " +
                    "SelectionDistributor has already been started");
        }
        Collection<Fpc> available;

        ExecutionConditions ec = query.getExecutionConditions();
        if (ec.getSpecs().isEmpty()) {
            available = registry.getAll();
        } else {
            available = registry.get(ec.getSpecs(),
                    Collections.emptyList());
        }

        try {
            for (Fpc fpc : available) {
                // TODO: filter out those fpcs that cannor run the query
                SelectionExecutor se = new SelectionExecutor(query, handler, fpc);
                active.put(fpc, se);
                se.start();
            }
        } catch (QueryException e) {
            // TODO: make this better
            throw new RuntimeException("unimplemented");
        }

        status = RUNNING;
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
        public void error(Refresh source, Throwable cause) {

        }

        @Override
        public void data(Refresh source, Void value) {

        }

    }

}
