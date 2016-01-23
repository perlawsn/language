package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.query.statement.GroupBy;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.Select;
import org.dei.perla.lang.query.statement.SelectionStatement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages the execution of one or more
 * {@link SelectionExecutor}s. Each {@link SelectionExecutor} is responsible
 * for managing a single {@link GroupBy} group.
 *
 * @author Guido Rota 24/10/15.
 */
public final class SelectionManager {

    private static final int READY = 0;
    private static final int RUNNING = 1;
    private static final int STOPPED = 2;

    private final SelectionStatement query;
    private final Select select;
    private final GroupBy groupBy;
    private final Sampling sampling;

    private final Lock lk = new ReentrantLock();
    private int state = READY;

    private final ReadWriteLock executorsLock = new ReentrantReadWriteLock();
    private final SelectionExecutor selectionExecutor;
    private final Map<List<Object>, SelectionExecutor> executorGroups;

    private final SamplerManager samplerManager;

    public SelectionManager(
            SelectionStatement query,
            Fpc fpc,
            StatementHandler<SelectionStatement> handler) {
        this.query = query;
        this.select = query.getSelect();
        this.groupBy = select.getGroupBy();
        if (groupBy == GroupBy.NONE) {
            selectionExecutor = new SelectionExecutor(query, fpc, handler);
            executorGroups = null;
        } else {
            selectionExecutor = null;
            executorGroups = new HashMap<>();
        }
        this.sampling = query.getSampling();
        samplerManager = new SamplerManager(
                query,
                fpc,
                new SamplerHandler()
        );
    }

    public void start() {
        lk.lock();
        try {
            if (state == RUNNING) {
                return;
            } else if (state == STOPPED) {
                throw new IllegalStateException(
                        "Cannot restart selectionManager");
            }
            state = RUNNING;
            samplerManager.start();
        } finally {
            lk.unlock();
        }
    }

    public void stop() {
        lk.lock();
        try {
            if (state == STOPPED) {
                return;
            } else if (state == READY) {
                throw new IllegalArgumentException(
                        "SelectionManager has not been started");
            }
            state = STOPPED;
        } finally {
            lk.unlock();
        }
    }

    public boolean isRunning() {
        lk.lock();
        try {
            return state == RUNNING;
        } finally {
            lk.unlock();
        }
    }


    private final class SamplerHandler implements
            QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable cause) {
            if (groupBy == GroupBy.NONE) {
                selectionExecutor.error(source, cause);
            } else {
                relayErrorToGroups(source, cause);
            }
        }

        private void relayErrorToGroups(Sampling source, Throwable cause) {
            executorsLock.readLock().lock();
            try {
                executorGroups.values().forEach(sh -> sh.error(source, cause));
            } finally {
                executorsLock.readLock().lock();
            }
        }

        @Override
        public void data(Sampling source, Object[] data) {
            if (groupBy == GroupBy.NONE) {
                selectionExecutor.data(source, data);
            } else {
                relayDataToGroup(source, data);
            }
        }

        private void relayDataToGroup(Sampling source, Object[] data) {
            throw new RuntimeException("unimplemented");
        }

    }

}
