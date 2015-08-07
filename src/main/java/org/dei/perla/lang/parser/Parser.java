package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ast.ExpressionAST;
import org.dei.perla.lang.parser.ast.StatementAST;
import org.dei.perla.lang.persistence.StreamDriver;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.Statement;

import java.io.StringReader;
import java.util.Map;

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

        Statement s = parseStatement(ast, ctx);
        if (ctx.getErrorCount() > 0) {
            throw new StatementParseException(ctx.getErrorDescription());
        }

        return s;
    }

    protected Statement parseStatement(StatementAST s, ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

    protected Expression parseExpression(ExpressionAST exp,
            TypeClass bound, ParserContext ctx, Map<Attribute, Integer> atts) {
        return exp.compile(bound, ctx, atts);
    }

}
