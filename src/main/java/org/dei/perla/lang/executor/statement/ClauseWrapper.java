package org.dei.perla.lang.executor.statement;

import java.util.function.Consumer;

/**
 * @author Guido Rota 23/03/15.
 */
public final class ClauseWrapper<T extends Clause> {

    private final T clause;
    private final String error;

    public ClauseWrapper(T clause) {
        this.clause = clause;
        error = null;
    }

    public ClauseWrapper(T clause, String error) {
        this.clause = clause;
        this.error = error;
    }

    public T getClause() {
        return clause;
    }

    public boolean hasError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

}
