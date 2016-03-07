package org.dei.perla.lang;

import org.dei.perla.core.PerLaSystem;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.executor.SelectionQueryTask;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.parser.ParseException;
import org.dei.perla.lang.parser.ParserAST;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.ast.StatementAST;
import org.dei.perla.lang.query.statement.*;

import java.io.StringReader;

/**
 * Main entry point for the execution of PerLa queries
 *
 * @author Guido Rota 07/07/15.
 */
public final class Executor {

    private final PerLaSystem perla;

    public Executor(PerLaSystem perla) {
        this.perla = perla;
    }

    public StatementTask execute(Statement s, StatementHandler h) throws QueryException{
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
    
    public StatementTask execute(String query, StatementHandler h)
            throws QueryException {
        Errors err = new Errors();
        Statement s = parseQuery(query);
        return execute(s, h);
    }

    private Statement parseQuery(String query) throws QueryException {
        ParserContext ctx = new ParserContext();
        ParserAST p = new ParserAST(new StringReader(query));
        StatementAST ast;
        try {
            ast = p.Statement(ctx);
        } catch(ParseException e) {
            throw new QueryException("Cannot parse query", e);
        }
        if (ctx.hasErrors()) {
            throw new QueryException(ctx.getErrorDescription());
        }

        Statement s = ast.compile(ctx);
        if (ctx.hasErrors()) {
            throw new QueryException(ctx.getErrorDescription());
        }
        return s;
    }

    private StatementTask executeSelection(SelectionStatement sel,
            StatementHandler h) {
    	
    /*	List<Attribute> required = sel.getExecutionConditions().getAttributes();
        Collection<Fpc> fpcs = perla.getRegistry().getAll();//required, new ArrayList<Attribute>());
        List<SelectionExecutor> execs = new ArrayList<SelectionExecutor>(fpcs.size());
        SelectionExecutor e;
        SelectionDistributor sd;
        LatchingQueryHandler<Object, Object[]> ssh; */
        SelectionQueryTask sqt = new SelectionQueryTask(sel, perla.getRegistry(),(QueryHandler)  h);
        StatementTask st = new SelectionStatementTask(sqt);
        return st;
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
