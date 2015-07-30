package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 30/07/15.
 */
public final class CreationStatementAST extends NodeAST {

    private final String stream;
    private final List<FieldDefinitionAST> fields;
    private final SelectionStatementAST selection;

    public CreationStatementAST(Token token, String stream,
            List<FieldDefinitionAST> fields, SelectionStatementAST selection) {
        super(token);
        this.stream = stream;
        this.fields = Collections.unmodifiableList(fields);
        this.selection = selection;
    }

    public String getStream() {
        return stream;
    }

    public List<FieldDefinitionAST> getFields() {
        return fields;
    }

    public SelectionStatementAST getSelection() {
        return selection;
    }

}
