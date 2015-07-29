package org.dei.perla.lang;

import org.dei.perla.core.PerLaSystem;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.parser.ParseException;
import org.dei.perla.lang.parser.Parser;
import org.dei.perla.lang.query.statement.*;

import java.io.StringReader;

/**
 * Main entry point for the execution of PerLa queries
 *
 * @author Guido Rota 07/07/15.
 */
public class Executor {

    private final PerLaSystem perla;

    public Executor(PerLaSystem perla) {
        this.perla = perla;
    }

    public StatementTask execute(String query, StatementHandler h)
            throws QueryException {
        Errors err = new Errors();
        Parser p = new Parser(new StringReader(query));

        Statement s;
        try {
            s = p.Statement(err);
            if (!err.isEmpty()) {
                throw new QueryException(err.asString("Error parsing query"));
            }
        } catch(ParseException e) {
            throw new QueryException("Error while parsing query '" +
                    query + "'", e);
        }

        if (s instanceof SelectionStatement) {
            SelectionStatement sel = (SelectionStatement) s;
            return executeSelection(sel, h);

        } else if (s instanceof CreationStatement) {
            CreationStatement cre = (CreationStatement) s;
            return executeCreation(cre, h);

        } else if (s instanceof InsertionStatement) {
            InsertionStatement ins = (InsertionStatement) s;
            return executeInsertion(ins, h);

        } else if (s instanceof SetStatement) {
            SetStatement set = (SetStatement) s;
            return executeSet(set, h);

        } else {
            throw new RuntimeException("Unknown statement type " +
                    s.getClass().getName());
        }
    }

    private StatementTask executeSelection(SelectionStatement sel,
            StatementHandler h) {
        throw new RuntimeException("unimplemented");
    }

    private StatementTask executeCreation(CreationStatement cre,
            StatementHandler h) {
        throw new RuntimeException("unimplemented");
    }

    private StatementTask executeInsertion(InsertionStatement ins,
            StatementHandler h) {
        throw new RuntimeException("unimplemented");
    }

    private StatementTask executeSet(SetStatement set,
            StatementHandler h) {
        throw new RuntimeException("unimplemented");
    }

}
