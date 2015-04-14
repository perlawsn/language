package org.dei.perla.lang.executor;

/**
 * @author Guido Rota 10/04/15.
 */
public class QueryException extends Exception {

    private static final long serialVersionUID = -3374902279548108393L;

    public QueryException() {
        super();
    }

    public QueryException(String message) {
        super(message);
    }

    public QueryException(Throwable cause) {
        super(cause);
    }

    public QueryException(String message, Throwable cause) {
        super(message, cause);
    }

}
