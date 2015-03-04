package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.query.Query;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Guido Rota 04/03/15.
 */
public class Executor {

    private static ExecutorService pool = Executors.newCachedThreadPool();

    public static void execute(Query q, int samples, QueryHandler qh) {
        throw new RuntimeException("unimplemented");
    }

    public static void execute(Query q, Duration d, QueryHandler qh) {
        throw new RuntimeException("unimplemented");
    }

}
