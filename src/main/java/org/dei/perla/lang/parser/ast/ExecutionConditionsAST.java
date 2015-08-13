package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ExecutionConditionsAST extends NodeAST {

    private final ExpressionAST cond;
    private final NodeSpecificationsAST specs;
    private final RefreshAST refresh;

    public ExecutionConditionsAST(ExpressionAST cond,
            NodeSpecificationsAST specs, RefreshAST refresh) {
        this(null, cond, specs, refresh);
    }

    public ExecutionConditionsAST(Token token, ExpressionAST cond,
            NodeSpecificationsAST specs, RefreshAST refresh) {
        super(token);
        this.cond = cond;
        this.specs = specs;
        this.refresh = refresh;
    }

    public ExpressionAST getCondition() {
        return cond;
    }

    public NodeSpecificationsAST getSpecifications() {
        return specs;
    }

    public RefreshAST getRefresh() {
        return refresh;
    }

}
