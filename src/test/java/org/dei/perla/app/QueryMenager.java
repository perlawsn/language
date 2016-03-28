package org.dei.perla.app;

import org.dei.perla.core.PerLaSystem;
import org.dei.perla.lang.executor.QueryException;

import org.dei.perla.lang.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

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
		
		/*GENERO UN NOME CASUALE PER LA TABELLA
		 * Nel caso creassimo il registro query, avremmo il campo già compilato
		 * Credo che mettere un nome casuale sia l'unico modo per fare una distinzione
		 */
		
		
		String tableName = "query_"+generateRandString(10);
		
		/*
		 * PASSO IL NOME DELLA TABELLA AD UN APPOSITO COSTRUTTORE
		 */
		BaseHandler b= new BaseHandler(id, tableName);
		System.out.println(id);
		
		try {
		/*
		 * HO SDOPPIATO IL METODO EXECUTE
		 */
			queries.add(exec.execute(s, b, tableName));
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
	
	private String generateRandString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}
	
}
