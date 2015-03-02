package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.expression.Expression;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Guido Rota 02/03/15.
 */
public class ExecutionContext {

    private Map<Expression, Object> expCache;

    protected ExecutionContext() {
        expCache = new HashMap<>();
    }

    public void cacheValue(Expression e, Object v) {
        expCache.put(e, v);
    }

    public Object getCache(Expression e) {
        return expCache.get(e);
    }

}
