package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.utils.AttributeUtils;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

    private static final ScheduledExecutorService timer =
            Executors.newScheduledThreadPool(5);

    private final SelectionStatement query;
    private final Fpc fpc;
    private final StatementHandler handler;

    private final List<Attribute> selAtts;
    private final Select select;
    private final Expression where;
    private final GroupBy groupBy;
    private final Sampling sampling;
    private final WindowSize terminate;

    private int terminateCount;
    private final SelectionHandler selectionHandler = new SelectionHandler();
    private final TerminateHandler terminateHandler = new TerminateHandler();

    private final Lock lk = new ReentrantLock();
    private int state = READY;

    private final ReadWriteLock executorsLock = new ReentrantReadWriteLock();
    // Using a list instead of an array to enforce immutability
    private final List<Integer> groupAttributeIndex;
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

        this.selAtts = query.getAttributes();
        this.select = query.getSelect();
        this.where = query.getWhere();
        this.groupBy = query.getGroupBy();
        this.sampling = query.getSampling();
        this.terminate = query.getTerminate();

        if (groupBy == GroupBy.NONE) {
            selectionExecutor =
                    new SelectionExecutor(select, selAtts, selectionHandler);
            executorGroups = null;
            groupAttributeIndex = null;
        } else {
            selectionExecutor = null;
            executorGroups = new HashMap<>();
            groupAttributeIndex = computeGroupAttributeIndex(
                    query.getAttributes(), groupBy.getFields());
        }

        samplerManager = new SamplerManager(query, fpc, new SamplerHandler());
    }

    private List<Integer> computeGroupAttributeIndex(
            List<Attribute> queryAtts,
            List<String> groupAtts) {
        Integer[] idxs = new Integer[groupAtts.size()];
        for (int i = 0; i < groupAtts.size(); i++) {
            String id = groupAtts.get(i);
            idxs[i] = AttributeUtils.indexOf(queryAtts, id);
            if (idxs[i] == -1) {
                throw new RuntimeException(
                        "Group by attribute '" + id + "' not found in " +
                                "selection fields");
            }
        }
        return Collections.unmodifiableList(Arrays.asList(idxs));
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
            startTerminate();
            samplerManager.start();
        } finally {
            lk.unlock();
        }
    }

    private void startTerminate() {
        if (terminate == null) {
            return;
        }
        if (terminate.getType() == WindowSize.WindowType.SAMPLE) {
            terminateCount = terminate.getSamples();
        } else if (terminate.getType() == WindowSize.WindowType.TIME) {
            timer.schedule(
                    terminateHandler,
                    terminate.getDuration().toMillis(),
                    TimeUnit.MILLISECONDS
            );
        } else {
            throw new RuntimeException("Unknown TERMINATE AFTER type");
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


    private final class TerminateHandler implements Runnable {

        @Override
        public void run() {
            stop();
        }

    }


    /**
     * A {@link QueryHandler} used for collecting and distributing record
     * data generated by the individual {@link SelectionExecutor}s
     */
    private final class SelectionHandler implements
            QueryHandler<Select, Object[]> {

        @Override
        public void error(Select source, Throwable cause) {

        }

        @Override
        public void data(Select source, Object[] value) {

        }

    }


    /**
     * A {@link QueryHandler} used for collecting samples from the {@link Fpc}s
     * and relaying them to the {@link SelectionExecutor} responsible for the
     * associated GROUP BY group.
     */
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
            LogicValue v = (LogicValue) where.run(sample, null);
            if (v != LogicValue.TRUE) {
                return;
            }

            lk.lock();
            try {
                if (groupBy == GroupBy.NONE) {
                    selectionExecutor.data(source, sample);
                } else {
                    relayDataToGroup(source, sample);
                }
                checkTermination();
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
                        exec = new SelectionExecutor(
                                select,
                                selAtts,
                                selectionHandler
                        );
                        executorGroups.put(g, exec);
                    }
                } finally {
                    executorsLock.writeLock().unlock();
                }
            }

            exec.data(source, sample);
        }

        private Group createGroup(Object[] sample) {
            Object[] values = new Object[groupAttributeIndex.size()];
            for (int i = 0; i < groupAttributeIndex.size(); i++) {
                int si = groupAttributeIndex.get(i);
                values[i] = si;
            }
            return new Group(values);
        }

        private void checkTermination() {
            if (terminate != null &&
                    terminate.getType() != WindowSize.WindowType.SAMPLE) {
                return;
            }

            terminateCount--;
            if (terminateCount == 0) {
                stop();
            }
        }

    }

}
