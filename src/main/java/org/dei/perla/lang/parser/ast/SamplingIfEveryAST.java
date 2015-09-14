package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.RatePolicy;
import org.dei.perla.lang.query.statement.SamplingIfEvery;

import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class SamplingIfEveryAST extends SamplingAST {

    private final List<IfEveryAST> ifevery;
    private final RatePolicy policy;
    private final RefreshAST refresh;

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
        throw new RuntimeException("unimplemented");
    }

}
