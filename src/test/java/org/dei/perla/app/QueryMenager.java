package org.dei.perla.app;

import org.dei.perla.core.PerLaSystem;
import org.dei.perla.lang.executor.QueryException;

import org.dei.perla.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class QueryMenager implements Observer{
	 Executor exec;
	 PerLaSystem sys;
	 List<StatementTask> queries;
	 List<BaseHandler> data;
	 List<Observer> listeners;
	 int id=0;
	 
	 QueryMenager(PerLaSystem s){
		 sys=s;
		 exec= new Executor(sys);
		 ArrayList<Observer> listener= new ArrayList<Observer>();
		 listener.add(this);
		 queries= new ArrayList<StatementTask>();
		 data= new ArrayList<BaseHandler>();
		 if(queries==null)
			 System.out.println("");
		 System.out.println("ready");
	 }
	
	public synchronized void addQuery(String s){
		id++;
		BaseHandler b= new BaseHandler(id);
		System.out.println(id);
		
		try {
			queries.add(exec.execute(s, b));
		} catch (QueryException e) {
			e.printStackTrace();
		}
		b.addObserver(listeners);
		System.out.println(queries.toString());
		data.add(b);

	}
	
	public void addListener(Observer o){
		listeners.add(o);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}
}
