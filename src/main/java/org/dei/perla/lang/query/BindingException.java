package org.dei.perla.lang.query;

/**
 * @author Guido Rota 21/04/15.
 */
public class BindingException extends Exception {

    private static final long serialVersionUID = -7065377657472275218L;

    public BindingException() { }

    public BindingException(String msg) {
        super(msg);
    }

    public BindingException(Throwable cause) {
        super(cause);
    }

    public BindingException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
