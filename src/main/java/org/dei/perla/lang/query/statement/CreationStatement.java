package org.dei.perla.lang.query.statement;

import org.dei.perla.lang.persistence.StreamDefinition;

/**
 * @author Guido Rota 04/07/15.
 */
public class CreationStatement implements Statement {

    private final StreamDefinition stream;
    private final SelectionStatement sel;

    public CreationStatement(StreamDefinition stream, SelectionStatement sel) {
        this.stream = stream;
        this.sel = sel;
    }

    public StreamDefinition getStreamDefinition() {
        return stream;
    }

    public SelectionStatement getSelectionStatement() {
        return sel;
    }

}
