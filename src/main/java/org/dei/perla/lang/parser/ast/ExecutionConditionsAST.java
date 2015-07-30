package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.lang.parser.Token;

import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class ExecutionConditionsAST extends NodeAST {

    private final ExpressionAST cond;
    private final List<DataTemplate> specs;
    private final RefreshAST refresh;

    public ExecutionConditionsAST(Token token, ExpressionAST cond,
            List<DataTemplate> specs, RefreshAST refresh) {
        super(token);
        this.cond = cond;
        this.specs = Collections.unmodifiableList(specs);
        this.refresh = refresh;
    }

    public ExpressionAST getCondition() {
        return cond;
    }

    public List<DataTemplate> getSpecifications() {
        return specs;
    }

    public RefreshAST getRefresh() {
        return refresh;
    }

}
