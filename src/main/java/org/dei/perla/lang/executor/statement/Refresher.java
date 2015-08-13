package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.core.fpc.TaskHandler;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.sample.Sample;
import org.dei.perla.core.utils.AsyncUtils;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.query.statement.Refresh;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * REFRESH clause executor.
 *
 * This executor can be stopped and re-started at will.
 *
 * @author Guido Rota 24/04/15.
 */
public final class Refresher {

    private static final int STOPPED = 0;
    private static final int RUNNING = 1;
    private static final int ERROR = 2;

    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(12);

    private final Refresh refresh;
    private final QueryHandler<? super Refresh, Void> handler;
    private final Fpc fpc;

    private int status = STOPPED;

    private final TaskHandler evtHand = new EventHandler();

    private Task evtTask;
    private ScheduledFuture<?> timer;

    public Refresher(Refresh refresh,
            QueryHandler<? super Refresh, Void> handler, Fpc fpc) {

        this.refresh = refresh;
        this.handler = handler;
        this.fpc = fpc;
    }

    /**
     * Starts the execution of the {@link Refresh} clause. Startup errors
     * will be asynchronously notified through the {@link QueryHandler}
     * specified in the constructor after the {@code start()} method is
     * over.
     */
    public synchronized void start() {
        if (status != STOPPED) {
            return;
        }

        status = RUNNING;
        switch (refresh.getType()) {
            case TIME:
                startTimed();
                break;
            case EVENT:
                startEvent();
                break;
            default:
                throw new RuntimeException("Unexpected " + refresh.getType()
                        + " refresh type");
        }
    }

    /**
     * Starts the execution of a time-based Refresh clause
     */
    private void startTimed() {
        long period = refresh.getDuration().toMillis();
        timer = scheduler.scheduleAtFixedRate(() -> {
            handler.data(refresh, null);
        }, period, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Starts the execution of an event-based Refresh clause
     */
    private void startEvent() {
        List<Attribute> es = refresh.getEvents();
        evtTask = fpc.async(es, true, evtHand);

        if (evtTask == null) {
            status = ERROR;
            notifyErrorAsync(createStartupError(es));
        }
    }

    /**
     * Simple utility method employed to asynchronously propagate an error
     *
     * @param msg error message
     */
    private void notifyErrorAsync(String msg) {
        AsyncUtils.runInNewThread(() -> {
            synchronized (Refresher.this) {
                Exception e = new QueryException(msg);
                handler.error(refresh, e);
            }
        });
    }

    private String createStartupError(List<Attribute> events) {
        StringBuilder bld =
                new StringBuilder("Error starting REFRESH ON EVENT executor: ");
        bld.append("cannot retrieve events ");
        events.forEach(e -> bld.append(e).append(" "));
        bld.append("from FPC ").append(fpc.getId());
        return bld.toString();
    }

    /**
     * Stops executing the {@link Refresh} clause. The clause can be resumed
     * by invoking the {@code start()} method anew.
     */
    public synchronized void stop() {
        if (status != RUNNING) {
            return;
        }

        status = STOPPED;
        stopExecution();
    }

    /**
     * Stops the execution of the {@link Refresh} tasks and timers.
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
     */
    private void stopExecution() {
        if (evtTask != null) {
            evtTask.stop();
            evtTask = null;
        }
        if (timer != null) {
            timer.cancel(false);
            timer = null;
        }
    }

    /**
     * Indicates if the {@link Refresh} clause executor is running or not
     *
     * @return true if the executor is running, false otherwise
     */
    public synchronized boolean isRunning() {
        return status == RUNNING;
    }

    /**
     * Simple utility method employed to propagate an error status and stop
     * the sampler
     *
     * NOTE: This method is not thread safe, and should therefore only be
     * invoked with proper synchronization.
     *
     * @param msg error message
     * @param cause cause exception
     */
    private synchronized void handleError(String msg, Throwable cause) {
        status = ERROR;
        stopExecution();
        handler.error(refresh, new QueryException(msg, cause));
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
                if (status == RUNNING && task == evtTask) {
                    handleError("REFRESH ON EVENT sampling stopped " +
                                    "prematurely", null);
                }
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            synchronized(Refresher.this) {
                handler.data(refresh, null);
            }
        }

        @Override
        public void error(Task task, Throwable cause) {
            synchronized(Refresher.this) {
                if (status != RUNNING) {
                    return;
                }

                handleError("REFRESH ON EVENT sampling generated an error", cause);
            }
        }

    }

}
