package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.statement.Refresh;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Guido Rota 24/04/15.
 */
public final class Refresher {

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(12);

    private final Refresh refresh;
    private final QueryHandler<? super Refresh, Void> handler;
    private final Fpc fpc;

    private volatile boolean running = false;

    private final TaskHandler evtHand = new EventHandler();

    private Task evtTask;
    private ScheduledFuture<?> timer;

    public Refresher(Refresh refresh,
            QueryHandler<? super Refresh, Void> handler, Fpc fpc) {
        this.refresh = refresh;
        this.handler = handler;
        this.fpc = fpc;
    }

    public synchronized void start() {
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
        running = true;
    }

    private void startTimeRefresh() {
        long period = refresh.getDuration().toMillis();
        timer = scheduler.scheduleAtFixedRate(() -> {
            handler.data(refresh, null);
        }, period, period, TimeUnit.MILLISECONDS);
    }

    private void startEventRefresh() {
        List<Attribute> es = refresh.getEvents();
        evtTask = fpc.async(es, true, evtHand);

        if (evtTask == null) {
            throw new RuntimeException("Initialization of REFRESH ON EVENT " +
                    "sampling failed, cannot retrieve the required events");
        }
    }

    public synchronized void stop() {
        running = false;
        if (evtTask != null) {
            evtTask.stop();
            evtTask = null;
        }
        if (timer != null) {
            timer.cancel(false);
            timer = null;
        }
    }

    public synchronized boolean isRunning() {
        return running;
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
            synchronized(Refresher.this) {
                if (running && task == evtTask) {
                    handleError("REFRESH ON EVENT sampling stopped prematurely");
                }
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            // Not locking on purpose. We accept a weaker synchronization
            // guarantee in exchange for lower data latency
            if (!running) {
                return;
            }

            handler.data(refresh, null);
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized(Refresher.this) {
                if (!running) {
                    return;
                }

                handleError("REFRESH ON EVENT sampling generated an error", cause);
            }
        }

    }

}
