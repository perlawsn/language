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

    public static final String EVT_INIT_ERROR =
            "Initialization of REFRESH ON EVENT sampling failed, cannot " +
                    "retrieve the required events";

    private static final int RUNNING = 0;
    private static final int STOPPED = 1;

    private final Refresh refresh;
    private final QueryHandler<Refresh, Void> handler;
    private final Fpc fpc;

    private final AtomicInteger status = new AtomicInteger(STOPPED);

    private final TaskHandler evtHand = new EventHandler();

    private Task evtTask;
    private ScheduledFuture<?> timer;

    public Refresher(Refresh refresh, QueryHandler<Refresh, Void> handler,
            Fpc fpc) {
        this.refresh = refresh;
        this.handler = handler;
        this.fpc = fpc;
    }

    public void start() throws QueryException {
        if (!status.compareAndSet(STOPPED, RUNNING)) {
            return;
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
        if (!status.compareAndSet(RUNNING, STOPPED)) {
            return;
        }

        if (evtTask != null) {
            evtTask.stop();
        }
        if (timer != null) {
            timer.cancel(false);
        }
    }

    public boolean isRunning() {
        return status.intValue() == RUNNING;
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
            if (status.intValue() == RUNNING) {
                Exception e = new QueryException("REFRESH ON EVENT sampling " +
                        "stopped prematurely");
                handler.error(refresh, e);
            }
        }

        @Override
        public void data(Task task, Sample sample) {
            if (status.intValue() != RUNNING) {
                return;
            }

            handler.data(refresh, null);
        }

        @Override
        public void error(Task task, Throwable cause) {
            status.set(STOPPED);
            handler.error(refresh, cause);
        }

    }

}
