package org.dei.perla.lang.executor;

import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.parser.ParseException;
import org.dei.perla.lang.parser.ParserAST;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.ast.StatementAST;
import org.dei.perla.lang.query.statement.*;

import java.io.StringReader;

/**
 * @author Guido Rota 07/11/15.
 */
public final class QueryExecutor {

    public QueryTask execute(String query,
            QueryHandler<StatementTask, Record> handler) throws QueryException {
        Statement s = parseQuery(query);

        if (s instanceof SelectionStatement) {
            return executeSelection((SelectionStatement) s);
        } else if (s instanceof SetStatement) {
            return executeSet((SetStatement) s);
        } else if (s instanceof InsertionStatement) {
            return executeInsertion((InsertionStatement) s);
        } else if (s instanceof CreationStatement) {
            return executeCreate((CreationStatement) s);
        } else {
            throw new RuntimeException("Cannot run query " +
                    s.getClass().getSimpleName());
        }
    }

    private Statement parseQuery(String query) throws QueryException {
        ParserContext ctx = new ParserContext();
        ParserAST p = new ParserAST(new StringReader(query));
        StatementAST ast;
        try {
            ast = p.Statement(ctx);
        } catch(ParseException e) {
            throw new QueryException("Cannot parse query", e);
        }
        if (ctx.hasErrors()) {
            throw new QueryException(ctx.getErrorDescription());
        }

        Statement s = ast.compile(ctx);
        if (ctx.hasErrors()) {
            throw new QueryException(ctx.getErrorDescription());
        }
        return s;
    }

    private QueryTask executeSelection(SelectionStatement s) {
        throw new RuntimeException("unimplemented");
    }

    private QueryTask executeSet(SetStatement s) {
        throw new RuntimeException("unimplemented");
    }

    private QueryTask executeInsertion(InsertionStatement s) {
        throw new RuntimeException("unimplemented");
    }

    private QueryTask executeCreate(CreationStatement s) {
        throw new RuntimeException("unimplemented");
    }

}
