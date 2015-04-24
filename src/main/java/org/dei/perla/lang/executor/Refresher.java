package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.lang.executor.statement.Refresh;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Guido Rota 24/04/15.
 */
public final class Refresher {

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(12);

    private static final int INITIALIZING = 0;
    private static final int RUNNING = 1;
    private static final int STOPPING = 2;
    private static final int STOPPED = 3;

    private final Refresh refresh;
    private final QueryHandler<Refresh, Void> handler;
    private final Fpc fpc;

    private final AtomicInteger status = new AtomicInteger(STOPPED);

    private final TaskHandler evtHand = new EventHandler();

    private volatile Task evtTask;
    private volatile ScheduledFuture<?> timer;

    public Refresher(Refresh refresh, QueryHandler<Refresh, Void> handler,
            Fpc fpc) {
        this.refresh = refresh;
        this.handler = handler;
        this.fpc = fpc;
    }

    public void start() throws QueryException {
        while (!status.compareAndSet(STOPPED, INITIALIZING)) {
            if (isRunning()) {
                return;
            }
        }

        switch (refresh.getType()) {
            case TIME:
                startTimeRefresh();
                break;
            case EVENT:
                startEventRefresh();
                break;
            default:
                throw new RuntimeException("Unexpected " + refresh.getType()
                        + "refresh type");
        }

        status.set(RUNNING);
    }

    private void startTimeRefresh() throws QueryException {
        long period = refresh.getDuration().toMillis();
        timer = scheduler.scheduleAtFixedRate(() -> {
            handler.data(refresh, null);
        }, period, period, TimeUnit.MILLISECONDS);
    }

    private void startEventRefresh() throws QueryException {
        List<Attribute> es = refresh.getEvents();
        evtTask = fpc.async(es, true, evtHand);

        if (evtTask == null) {
            throw new QueryException("Initialization of REFRESH ON EVENT " +
                    "sampling failed, cannot retrieve the required events");
        }
    }

    public void stop() {
        while (!status.compareAndSet(RUNNING, STOPPING)) {
            if (!isRunning()) {
                return;
            }
        }

        if (evtTask != null) {
            Task t = evtTask;
            evtTask = null;
            t.stop();
        }
        if (timer != null) {
            ScheduledFuture<?> t = timer;
            timer = null;
            t.cancel(false);
        }

        status.set(STOPPED);
    }

    public boolean isRunning() {
        return status.intValue() <= RUNNING;
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * @param msg error message
     * @param cause cause exception
     */
    private void handleError(String msg, Throwable cause) {
        stop();
        handler.error(refresh, new QueryException(msg, cause));
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * @param msg error message
     */
    private void handleError(String msg) {
        handleError(msg, null);
    }


    /**
     * TaskHandler for managing the events that trigger the refresh clause
     * being executed
     *
     * @author Guido Rota 24/04/2015
     */
    private class EventHandler implements TaskHandler {

        @Override
        public void complete(Task task) {
            if (isRunning() && task == evtTask) {
                handleError("REFRESH ON EVENT sampling stopped prematurely");
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            if (!isRunning()) {
                return;
            }

            handler.data(refresh, null);
        }

        @Override
        public void error(Task task, Throwable cause) {
            if (!isRunning()) {
                return;
            }

            handleError("REFRESH ON EVENT sampling generated an error", cause);
        }

    }

}
