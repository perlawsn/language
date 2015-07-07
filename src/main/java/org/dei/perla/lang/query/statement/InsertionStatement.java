package org.dei.perla.lang.query.statement;

import java.util.Collections;
import java.util.List;

/**
 * @author Guido Rota 07/07/15.
 */
public final class InsertionStatement implements Statement {

    private final String stream;
    private final List<String> fields;
    private final SelectionStatement sel;

    public InsertionStatement(String stream, List<String> fields,
            SelectionStatement sel) {
        this.stream = stream;
        this.fields = Collections.unmodifiableList(fields);
        this.sel = sel;
    }

    public InsertionStatement(String stream, SelectionStatement sel) {
        this.stream = stream;
        this.fields = null;
        this.sel = sel;
    }

    public String getStream() {
        return stream;
    }

    public List<String> getFields() {
        return fields;
    }

    public SelectionStatement getSelectionStatement() {
        return sel;
    }

}
