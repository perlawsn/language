package org.dei.perla.lang.parser;

/**
 * @author Guido Rota 17/04/15.
 */
public class StatementParseException extends Exception {

    private static final long serialVersionUID = -8730467304973987914L;

    public StatementParseException() {
        super();
    }

    public StatementParseException(String msg) {
        super(msg);
    }

    public StatementParseException(Throwable cause) {
        super(cause);
    }

    public StatementParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
