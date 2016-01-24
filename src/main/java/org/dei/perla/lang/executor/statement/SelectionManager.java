package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.utils.AttributeUtils;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.query.statement.GroupBy;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.Select;
import org.dei.perla.lang.query.statement.SelectionStatement;

import java.util.Arrays;
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
    private final StatementHandler handler;
    private final Fpc fpc;

    private final Select select;
    private final GroupBy groupBy;
    private final Sampling sampling;

    private final Integer[] groupAttributeIndex;

    private final Lock lk = new ReentrantLock();
    private int state = READY;

    private final ReadWriteLock executorsLock = new ReentrantReadWriteLock();
    private final SelectionExecutor selectionExecutor;
    private final Map<Group, SelectionExecutor> executorGroups;

    private final SamplerManager samplerManager;

    public SelectionManager(
            SelectionStatement query,
            Fpc fpc,
            StatementHandler<SelectionStatement> handler) {
        this.query = query;
        this.fpc = fpc;
        this.handler = handler;

        this.select = query.getSelect();
        this.groupBy = select.getGroupBy();
        if (groupBy == GroupBy.NONE) {
            selectionExecutor = new SelectionExecutor(query, fpc, handler);
            executorGroups = null;
            groupAttributeIndex = null;
        } else {
            selectionExecutor = null;
            executorGroups = new HashMap<>();
            groupAttributeIndex = computeGroupAttributeIndex(
                    query.getAttributes(), groupBy.getFields());
        }
        this.sampling = query.getSampling();

        samplerManager = new SamplerManager(
                query,
                fpc,
                new SamplerHandler()
        );
    }

    private Integer[] computeGroupAttributeIndex(
            List<Attribute> queryAtts,
            List<String> groupAtts) {
        Integer[] idxs = new Integer[groupAtts.size()];
        for (int i = 0; i < groupAtts.size(); i++) {
            String id = groupAtts.get(i);
            idxs[i] = AttributeUtils.indexOf(queryAtts, id);
            if (idxs[i] == -1) {
                throw new RuntimeException(
                        "Group by attribute '" + id + "' not found");
            }
        }
        return idxs;
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


    /**
     * A subset of the sample values employed to identify an individual GROUP
     * BY group.
     *
     * @author Guido Rota (2016)
     */
    private final class Group {

        // Values associated to the group
        private final Object[] values;

        private Group(Object[] values) {
            this.values = values;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Group)) {
                return false;
            }

            return Arrays.equals(values, ((Group) o).values);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(values);
        }

    }


    private final class SamplerHandler implements
            QueryHandler<Sampling, Object[]> {

        @Override
        public void error(Sampling source, Throwable cause) {
            lk.lock();
            try {
                if (groupBy == GroupBy.NONE) {
                    selectionExecutor.error(source, cause);
                } else {
                    relayErrorToGroups(source, cause);
                }
            } finally {
                lk.unlock();
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
        public void data(Sampling source, Object[] sample) {
            lk.lock();
            try {
                if (groupBy == GroupBy.NONE) {
                    selectionExecutor.data(source, sample);
                } else {
                    relayDataToGroup(source, sample);
                }
            } finally {
                lk.unlock();
            }
        }

        private void relayDataToGroup(Sampling source, Object[] sample) {
            Group g = createGroup(sample);
            SelectionExecutor exec;
            executorsLock.readLock().lock();
            try {
                exec = executorGroups.get(g);
            } finally {
                executorsLock.readLock().unlock();
            }

            if (exec == null) {
                executorsLock.writeLock().lock();
                try {
                    exec = executorGroups.get(g);
                    if (exec == null) {
                        exec = new SelectionExecutor(query, fpc, handler);
                        executorGroups.put(g, exec);
                    }
                } finally {
                    executorsLock.writeLock().unlock();
                }
            }

            exec.data(source, sample);
        }

        private Group createGroup(Object[] sample) {
            Object[] values = new Object[groupAttributeIndex.length];
            for (int i = 0; i < groupAttributeIndex.length; i++) {
                int si = groupAttributeIndex[i];
                values[i] = si;
            }
            return new Group(values);
        }

    }

}
