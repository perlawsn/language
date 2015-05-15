package org.dei.perla.lang.executor;

import org.apache.log4j.Logger;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.executor.statement.SelectionExecutor;
import org.dei.perla.lang.query.BindingException;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.SelectionQuery;

import java.util.*;

/**
 * Distributes a {@link SelectionQuery} among the available FPCs and tracks
 * its progresses.
 *
 * @author Guido Rota 30/04/15.
 */
public class SelectionDistributor {

    private static final int NEW = 0;
    private static final int RUNNING = 1;
    private static final int STOPPED = 2;

    private final String name;
    private final SelectionQuery query;
    private final ExecutionConditions ec;
    private final QueryHandler<? super SelectionQuery, Object[]> handler;
    private final Registry registry;

    private volatile int status = NEW;

    private final List<SelectionExecutor> execs = new ArrayList<>();
    private final Set<Fpc> managed = new HashSet<>();

    protected SelectionDistributor(String name, SelectionQuery query,
            QueryHandler<? super SelectionQuery, Object[]> handler,
            Registry registry) {
        this.name = name;
        this.query = query;
        ec = query.getExecutionConditions();
        this.handler = handler;
        this.registry = registry;
    }

    public synchronized void start() {
        if (status != NEW) {
            throw new IllegalStateException("Cannot start, " +
                    "SelectionDistributor has already been started");
        }

        distribute();
        status = RUNNING;
    }

    private void distribute() {
        Collection<Fpc> fpcs;

        if (ec.getSpecs().isEmpty()) {
            fpcs = registry.getAll();
        } else {
            fpcs = registry.get(ec.getSpecs(),
                    Collections.emptyList());
        }

        for (Fpc fpc : fpcs) {
            if (managed.contains(fpc)) {
                continue;
            }

            try {
                SelectionQuery q = query.bind(fpc.getAttributes());
                SelectionExecutor se = new SelectionExecutor(q, handler, fpc);
                execs.add(se);
                se.start();
                managed.add(fpc);
            } catch (BindingException e) {
                // TODO: Ignore silently? do something else??
            }
        }
    }

    public synchronized void stop() {
        if (status == STOPPED) {
            return;
        }

        execs.forEach(SelectionExecutor::stop);
        status = STOPPED;
    }

    public synchronized boolean isRunning() {
        return status == RUNNING;
    }


    /**
     * Handler for the EXECUTE IF refresh condition. When triggered, the
     * registry is queried to check if the query can be started on new FPCs
     *
     * @author Guido Rota 29/04/2015
     */
    private class RefreshHandler implements QueryHandler<Refresh, Void> {

        @Override
        public void error(Refresh source, Throwable cause) {
            synchronized (SelectionDistributor.this) {
                stop();
                Exception e = new QueryException("Error while evaluating " +
                        "REFRESH clause in EXECUTE IF condition", cause);
                handler.error(query, e);
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
