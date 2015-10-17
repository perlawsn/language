package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.ExecutionConditions;
import org.dei.perla.lang.query.statement.Refresh;

import java.util.List;
import java.util.Set;

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

    /**
     * Compiles the {@code ExecutionConditionsAST} into an executable class
     *
     * @param ctx context object employed to store intermediate parsing results
     * @return runnable {@link ExecutionConditions} class
     */
    public ExecutionConditions compile(ParserContext ctx) {
        AttributeOrder attOrd = new AttributeOrder();
        Expression condComp = cond.compile(DataType.BOOLEAN, ctx, attOrd);
        if (condComp instanceof Constant &&
                ((LogicValue) condComp.run(null, null)).toBoolean() == false) {
            ctx.addError("Execution condition always evaluates to false");
            return null;
        }
        Refresh refComp = refresh.compile(ctx);

        Set<Attribute> specAtts = specs.compile(ctx);
        List<Attribute> atts = attOrd.toList(ctx);
        return new ExecutionConditions(specAtts, condComp, atts, refComp);
    }

}
