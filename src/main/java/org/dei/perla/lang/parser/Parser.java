package org.dei.perla.lang.parser;

import org.dei.perla.lang.parser.ast.StatementAST;
import org.dei.perla.lang.persistence.StreamDriver;
import org.dei.perla.lang.query.statement.Statement;

import java.io.StringReader;

/**
 * @author Guido Rota 30/07/15.
 */
public final class Parser {

    private final StreamDriver streams;

    public Parser(StreamDriver streams) {
        this.streams = streams;
    }

    public Statement parser(String text) throws StatementParseException {
        ParserContext ctx = new ParserContext();
        StatementAST ast;

        ParserAST p = new ParserAST(new StringReader(text));
        try {
            ast = p.Statement(ctx);
        } catch(ParseException e) {
            throw new StatementParseException("Syntax error", e);
        }

        Statement s = ast.compile(ctx);
        if (ctx.getErrorCount() > 0) {
            throw new StatementParseException(ctx.getErrorDescription());
        }

        return s;
    }

}
