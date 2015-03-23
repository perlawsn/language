package org.dei.perla.lang.executor.statement;

import java.util.function.Consumer;

/**
 * @author Guido Rota 23/03/15.
 */
public final class ClauseWrapper<T extends Clause> {

    private final T clause;
    private final String error;

    public ClauseWrapper(T clause, String error) {
        this.clause = clause;
        this.error = error;
    }

    public T getClause(Consumer<String> onError) {
        if (error != null) {
            onError.accept(error);
        }
        return clause;
    }

}
