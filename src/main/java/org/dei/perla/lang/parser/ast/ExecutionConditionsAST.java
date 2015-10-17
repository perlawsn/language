package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.parser.ast.NodeSpecificationsAST.NodeSpecificationsType;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;

import java.util.List;

/**
 * Execution condition Abstract Syntax Tree node
 *
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

    public ExecutionConditions compile(List<Attribute> queryAtts,
            ParserContext ctx) {
        AttributeOrder attOrd = new AttributeOrder();
        Expression condComp = cond.compile(DataType.BOOLEAN, ctx, attOrd);
        if (condComp instanceof Constant &&
                ((LogicValue) condComp.run(null, null)).toBoolean() == false) {
            ctx.addError("Execution condition always evaluates to false");
            return null;
        }
        Refresh refComp = refresh.compile(ctx);

        List<Attribute> specAtts = queryAtts;
        if (specs.getType() != NodeSpecificationsType.ALL) {
            specAtts = specs.getSpecifications();
        }

        List<Attribute> atts = attOrd.toList(ctx);
        return new ExecutionConditions(specAtts, condComp, atts, refComp);
    }

}
