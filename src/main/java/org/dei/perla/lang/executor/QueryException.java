package org.dei.perla.lang.executor;

/**
 * @author Guido Rota 10/04/15.
 */
public class QueryException extends Exception {

    public QueryException() {
        super();
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
