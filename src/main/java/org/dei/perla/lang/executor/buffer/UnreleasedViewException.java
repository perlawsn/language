package org.dei.perla.lang.executor.buffer;

/**
 * @author Guido Rota 23/10/15.
 */
public class UnreleasedViewException extends Exception {

    private static final long serialVersionUID = -8604522011675051039L;

    public UnreleasedViewException() {
        super();
    }

    public UnreleasedViewException(String message) {
        super(message);
    }

    public UnreleasedViewException(String message, Throwable cause) {
        super(message, cause);
    }

}
