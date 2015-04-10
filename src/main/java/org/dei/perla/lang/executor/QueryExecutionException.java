package org.dei.perla.lang.executor;

/**
 * @author Guido Rota 10/04/15.
 */
public class QueryExecutionException extends Exception {

    public QueryExecutionException() {
        super();
    }

    public QueryExecutionException(String message) {
        super(message);
    }

    public QueryExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}
