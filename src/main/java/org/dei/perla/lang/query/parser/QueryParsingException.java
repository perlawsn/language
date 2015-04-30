package org.dei.perla.lang.query.parser;

/**
 * @author Guido Rota 17/04/15.
 */
public class QueryParsingException extends Exception {

    private static final long serialVersionUID = -8730467304973987914L;

    public QueryParsingException() {
        super();
    }

    public QueryParsingException(String msg) {
        super(msg);
    }

    public QueryParsingException(Throwable cause) {
        super(cause);
    }

    public QueryParsingException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
