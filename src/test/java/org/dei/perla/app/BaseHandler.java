package org.dei.perla.app;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.*;
import org.dei.perla.lang.database.DatabaseClass;
import org.dei.perla.lang.executor.Record;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;

public class BaseHandler extends Observable implements StatementHandler{

	private ArrayList<Observer> observers;
	private int count=0;
	private int id;
	/*
	 * Ogni BaseHandler ha la sua tabella
	 */
	private String tableName;
	private final Lock lk = new ReentrantLock();
	private DatabaseClass db;
	public BaseHandler(int i) {
		id=i;
	}
	
	public BaseHandler(int i, String tableName) {
		id=i;
		this.tableName=tableName;
	}


	public synchronized void data(Statement s, Object r) {
		count++;
		if(s instanceof SelectionStatement){
			
			for(Object o:(Object[])r){
				 Class<? extends Object> d;
				 d=o.getClass();
				 d.cast(o);
				 System.out.println("Get data queryn: "+this.id+" samplen :"+this.count+" value :"+o+"type :"+o.getClass().getSimpleName());
				 
			}
					
		}else
		{
			System.out.println("Not implemented");
		}
	}


	public void complete(Statement s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(Statement s, Throwable cause) {
		// TODO Auto-generated method stub
		
	}
	
	public void setObservers(ArrayList<Observer> observers) {
		this.observers = observers;
	}
	
	public void notifyObservers(Observable observable, List<Statement> blocks) {
		 for (Observer ob : observers) {
             ob.update(observable, blocks);
      }
	}

	public void addObserver(List<Observer> listener) {
		observers=(ArrayList<Observer>) listener;
		
	}


	public void data(Statement s, Record r) {
		 Class<? extends Object> d;
		 d=r.getValues().getClass();
		 d.cast(r.getValues());
		 for(Object o: r.getValues())
		System.out.println(this.id+" "+o );
		/*
		 * QUANDO ARRIVANO I DATI COSTRUISCO LA QUERY E LI INSERISCO NEL DATABASE
		 * Per controllare l'inserimento vai sulla riga di comando di mysql
		 * scrivi SELECT * FROM nometabella che ti compare nella console di eclipse
		
		
		if (db==null){
		db=new DatabaseClass();
		try {
			db.connect();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		String insertion = "INSERT INTO "+tableName +"(";
		
		int numValues=r.getValues().length;
		
		for (int i=0; i<numValues; i++){
			if (i!=numValues-1){
			insertion=r.getFields().get(i).getId()+",";
			}else{
			insertion=insertion+r.getFields().get(i).getId();
			}
		}
		insertion=insertion+") VALUES (";
		
		for (int i=0; i<numValues; i++){
			if (i!=numValues-1){
			insertion=insertion+r.getValues()[i]+",";
			}else{
			insertion=insertion+r.getValues()[i];
			}
		}
		
		insertion=insertion+");";
		System.out.println(insertion);
		try {
			db.saveData(insertion);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 */
	}

	public void data(Statement s, Object[] r) {
		// TODO Auto-generated method stub
		System.out.println(s.toString()+" "+r.toString());
		
		
	}

	@Override
	public void complete() {
		// TODO Auto-generated method stub
		
	}





}
