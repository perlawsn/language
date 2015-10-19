package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

import java.util.List;

/**
 * Group by Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public final class GroupByAST extends NodeAST {

    private final List<String> fields;

    public GroupByAST(List<String> fields) {
        this(null, fields);
    }

    public GroupByAST(Token token, List<String> fields) {
        super(token);
        this.fields = fields;
    }

    public List<String> getFields() {
        return fields;
    }

}
