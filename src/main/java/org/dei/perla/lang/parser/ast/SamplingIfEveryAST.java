package org.dei.perla.lang.parser.ast;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.parser.AttributeOrder;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.expression.Constant;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.IfEvery;
import org.dei.perla.lang.query.statement.RatePolicy;
import org.dei.perla.lang.query.statement.Refresh;
import org.dei.perla.lang.query.statement.SamplingIfEvery;

import java.util.List;

/**
 * Time based samling clause Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class SamplingIfEveryAST extends SamplingAST {

    private final List<IfEveryAST> ifevery;
    private final RatePolicy policy;
    private final RefreshAST refresh;

    public SamplingIfEveryAST(List<IfEveryAST> ifevery,
            RatePolicy policy, RefreshAST refresh) {
        this(null, ifevery, policy, refresh);
    }

    public SamplingIfEveryAST(Token token, List<IfEveryAST> ifevery,
            RatePolicy policy, RefreshAST refresh) {
        super(token);
        this.ifevery = ifevery;
        this.policy = policy;
        this.refresh = refresh;
    }

    public List<IfEveryAST> getIfEvery() {
        return ifevery;
    }

    public RatePolicy getRatePolicy() {
        return policy;
    }

    public RefreshAST getRefresh() {
        return refresh;
    }

    public SamplingIfEvery compile(ParserContext ctx) {
        AttributeOrder ord = new AttributeOrder();
        IfEvery ife = compileIfEvery(ord, ctx);
        Refresh rc = refresh.compile(ctx);

        return new SamplingIfEvery(ife, policy, rc, ord.toList(ctx));
    }

    private IfEvery compileIfEvery(AttributeOrder ord, ParserContext ctx) {
        IfEvery ife = null;
        IfEvery prev = null;
        for (int i = ifevery.size() - 1; i >= 0; i--) {
            ife = compileIfEvery(ifevery.get(i), ord, ctx, prev);
            prev = ife;
        }
        return ife;
    }

    private IfEvery compileIfEvery(IfEveryAST ife, AttributeOrder ord,
            ParserContext ctx, IfEvery prev) {
        Expression cond = ife.getCondition()
                .compile(DataType.BOOLEAN, ctx, ord);
        if (cond.equals(Constant.FALSE)) {
            ctx.addError("Condition in if-every clause at " +
                    ife.getPosition() + " always evaluates to false");
        }

        Expression value = ife.getEvery().getValue()
                .compile(DataType.NUMERIC, ctx, ord);
        if (value instanceof Constant) {
            int v = (int) ((Constant) value).getValue();
            if (v <= 0) {
                ctx.addError("Value in if-every clause at " + ife.getPosition() +
                        " is negative (only positive values are allowed)");
            }
        }

        return new IfEvery(cond, value, ife.getEvery().getUnit(), prev);
    }

}
