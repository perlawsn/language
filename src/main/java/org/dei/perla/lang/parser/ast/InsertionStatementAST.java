package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.Statement;

import java.util.Collections;
import java.util.List;

/**
 * Insertion statement Abstract Syntax Tree node.
 *
 * @author Guido Rota 30/07/15.
 */
public final class InsertionStatementAST extends StatementAST {

    private final String stream;
    private final List<String> fields;
    private final SelectionStatementAST selection;

    public InsertionStatementAST(String stream, List<String> fields,
            SelectionStatementAST selection) {
        this(null, stream, fields, selection);
    }

    public InsertionStatementAST(Token token, String stream,
            List<String> fields, SelectionStatementAST selection) {
        super(token);
        this.stream = stream;
        this.fields = Collections.unmodifiableList(fields);
        this.selection = selection;
    }

    public String getStream() {
        return stream;
    }

    public List<String> getFields() {
        return fields;
    }

    public SelectionStatementAST getSelection() {
        return selection;
    }

    @Override
    public Statement compile(ParserContext ctx) {
        throw new RuntimeException("unimplemented");
    }

}
