package org.dei.perla.app;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.*;
import org.dei.perla.lang.executor.Record;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.Statement;

public class BaseHandler extends Observable implements StatementHandler{

	private ArrayList<Observer> observers;
	private int count=0;
	private int id;
	private final Lock lk = new ReentrantLock();
	
	public BaseHandler(int i) {
		id=i;
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
		for(Object a:r.getValues()){
			System.out.println(a.toString()+"\n");
		}
		
	}

	public void data(Statement s, Object[] r) {
		// TODO Auto-generated method stub
		System.out.println(s.toString()+" "+r.toString());
		
		
	}


}
