package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public ExecutionConditions compile(List<Attribute> atts,
            ParserContext ctx) {
        Map<Attribute, Integer> attMap = new HashMap<>();
        Expression condComp = cond.compile(DataType.BOOLEAN, ctx, attMap);


        Refresh refComp = refresh.compile(ctx);
        throw new RuntimeException("unimplemented");
    }

}
