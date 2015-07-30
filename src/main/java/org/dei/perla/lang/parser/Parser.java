package org.dei.perla.lang.parser;

import org.dei.perla.lang.persistence.StreamDriver;
import org.dei.perla.lang.query.statement.Statement;

/**
 * @author Guido Rota 30/07/15.
 */
public final class Parser {

    private final StreamDriver streams;

    public Parser(StreamDriver streams) {
        this.streams = streams;
    }

    public Statement parser(String text) {
        throw new RuntimeException("unimplemented");
    }

}
