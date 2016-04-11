	package org.dei.perla.lang;

import org.dei.perla.core.PerLaSystem;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.database.DatabaseClass;
import org.dei.perla.lang.executor.CreationManager;

import org.dei.perla.lang.executor.InsertionManager;
import org.dei.perla.lang.executor.QueryException;
import org.dei.perla.lang.executor.SelectionManager;

import org.dei.perla.lang.executor.SetManager;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.parser.ParseException;
import org.dei.perla.lang.parser.ParserAST;
import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.ast.StatementAST;
import org.dei.perla.lang.query.statement.*;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry point for the execution of PerLa queries
 *
 * @author Guido Rota 07/07/15.
 */
public final class Executor {

    private final PerLaSystem perla;
    private Map<String,List<Statement>> queries;
    private CreationManager cm;
    private SelectionManager sm;
    private InsertionManager im;
    private SetManager setm;
    
    public Executor(PerLaSystem perla) {
        this.perla = perla;
        this.queries= new HashMap<String,List<Statement>>();
        this.cm = new CreationManager(perla.getRegistry());
        this.sm= new SelectionManager(perla.getRegistry());
        this.im= new InsertionManager();
        this.setm = new SetManager(perla.getRegistry());
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
        Statement s = parseQuery(query);
        SelectionStatement sels  = (SelectionStatement)s;
            
        
        return execute(s, h);
    }
    
    public StatementTask execute(String query, StatementHandler h, String tableName)
            throws QueryException {
        Statement s = parseQuery(query);
        SelectionStatement sels  = (SelectionStatement)s;
        /*
         * CREO LA TABELLA
        
        DatabaseClass db=new DatabaseClass();
        try {
			db.connect();
			db.createTable(sels.getAttributes(), tableName );
			db.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
         */
        
        return execute(s, h);
    }

    public Statement parseQuery(String query) throws QueryException {
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
    	return sm.insertQuery(sel, h);
      }
    	
    

    private StatementTask executeCreation(CreationStatement cre,
            StatementHandler h) {
    	return cm.insertQuery(cre, h);
    }

    private StatementTask executeInsertion(InsertionStatement ins,
            StatementHandler h) {
        return im.insertQuery(ins, h);
    }

    private StatementTask executeSet(SetStatement set,
            StatementHandler h) {
    	return setm.insertQuery(set, h);
       /* StatementTask setTask = new SetTask(set, perla.getRegistry());
        return setTask;*/
    }

}
