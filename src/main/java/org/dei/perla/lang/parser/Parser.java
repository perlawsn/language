package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.parser.ast.*;
import org.dei.perla.lang.persistence.StreamDriver;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.CreationStatement;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;
import org.dei.perla.lang.query.statement.WindowSize;

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

    protected Statement parseStatement(StatementAST stm, ParserContext ctx) {
        if (stm instanceof CreationStatementAST) {
            CreationStatementAST create = (CreationStatementAST) stm;
            return parseCreationStatement(create, ctx);
        } else if (stm instanceof InsertionStatementAST) {
            InsertionStatementAST insert = (InsertionStatementAST) stm;
            return parseInsertionStatement(insert, ctx);
        } else if (stm instanceof SetStatementAST) {
            SetStatementAST set = (SetStatementAST) stm;
            return parseSetStatement(set, ctx);
        } else {
            throw new RuntimeException("Unknown statement " +
                    stm.getClass().getSimpleName());
        }
    }

    protected CreationStatement parseCreationStatement(
            CreationStatementAST create, ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

    protected CreationStatement parseInsertionStatement(
            InsertionStatementAST insert, ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

    protected CreationStatement parseSetStatement(
            SetStatementAST set, ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

    protected SelectionStatement parseSelectionStatement(
            SelectionStatementAST sel, ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

    protected Expression parseExpression(ExpressionAST exp,
            TypeClass bound, ParserContext ctx, Map<Attribute, Integer> atts) {
        return exp.compile(bound, ctx, atts);
    }

}
