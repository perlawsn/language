package org.dei.perla.lang.executor;

import org.dei.perla.core.fpc.Fpc;
import org.dei.perla.core.fpc.Task;
import org.dei.perla.lang.query.Query;

/**
 * @author Guido Rota 24/02/15.
 */
public class Executor {

    private static final int DEFAULT_BUFFER_LENGTH = 512;

    private final Query query;
    private final Fpc fpc;

    private Task selectTask;

    public static void run(Query query, Fpc fpc) {

    }

    private Executor(Query query, Fpc fpc) {
        this.query = query;
        this.fpc = fpc;
    }

}
