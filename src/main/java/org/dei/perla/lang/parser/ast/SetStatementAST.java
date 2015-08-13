package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.Statement;

import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class SetStatementAST extends StatementAST {

    private final List<SetParameterAST> params;
    private final List<Integer> ids;

    public SetStatementAST(List<SetParameterAST> params, List<Integer> ids) {
        this(null, params, ids);
    }

    public SetStatementAST(Token token, List<SetParameterAST> params,
            List<Integer> ids) {
        super(token);
        this.params = Collections.unmodifiableList(params);
        this.ids = Collections.unmodifiableList(ids);
    }

    public List<SetParameterAST> getParameters() {
        return params;
    }

    public List<Integer> getIds() {
        return ids;
    }

    @Override
    public Statement compile(ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
