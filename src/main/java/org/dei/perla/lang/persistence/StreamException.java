package org.dei.perla.lang.persistence;

/**
 * @author Guido Rota 03/07/15.
 */
public class StreamException extends Exception {

    private static final long serialVersionUID = 7107909547236537441L;

    public StreamException() {
        super();
    }

    public StreamException(String msg) {
        super(msg);
    }

    public StreamException(Throwable cause) {
        super(cause);
    }

    public StreamException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
