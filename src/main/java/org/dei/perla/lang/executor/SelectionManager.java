package org.dei.perla.lang.executor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.registry.Registry;
import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.StatementTask;
import org.dei.perla.lang.query.expression.Aggregate;
import org.dei.perla.lang.query.expression.AttributeReference;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.statement.Select;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.WindowSize;


public final class SelectionManager {

	private ArrayList<SelectionStatement> query = new ArrayList<SelectionStatement>();
	private ArrayList<StatementHandler> handlers = new ArrayList<StatementHandler>();
	private ArrayList<SelectionDistributor> distributors = new ArrayList<SelectionDistributor>();
	private Registry registry;
	private ArrayList<SelectionStatementTask> stasks = new ArrayList<SelectionStatementTask>();
	private ArrayList<SelectionQueryTask> qtasks = new ArrayList<SelectionQueryTask>();
	private ArrayList<SelectionQueryHandler> qhandler = new ArrayList<SelectionQueryHandler>();
	public SelectionManager(Registry registry) {
		this.registry= registry;
	}
	
	public synchronized StatementTask insertQuery(SelectionStatement  sel, StatementHandler h) {
		handlers.add(h);
		SelectionStatementTask sst = new SelectionStatementTask();
		queryPreprocessing(sel,h);	
		SelectionQueryTask sqt = new SelectionQueryTask();
		qtasks.add(sqt);
		return sst;
	}
	

	private void queryPreprocessing(SelectionStatement sel, StatementHandler h) {
		Boolean multi=false;
		SelectionDistributor sd = null;
		for(Expression e:sel.getSelect().getFields()){
			if(e instanceof Aggregate )
				multi=true;
			else{
				multi=false;
				continue;
			}
		}
		if(multi){
			List<Expression> fields = new ArrayList<Expression>();
			int fpcs = registry.get(sel.getExecutionConditions().getSpecifications(), Collections.emptyList()).size();
			AttributeReference ar;
			Attribute a;
			WindowSize ws= sel.getEvery();
			for(int i=0;i<sel.getSelect().getFields().size();i++){
				a = sel.getAttributes().get(i);
				ar = new AttributeReference(a.getId(), a.getType(),i);
				fields.add(ar);
			}
			Select s = new Select(fields, sel.getSelect().getUpTo(),
					sel.getSelect().getGroupBy(),
					sel.getSelect().getHaving(),
					sel.getSelect().getDefault());
			SelectionStatement tmp = new SelectionStatement(s, 
					sel.getAttributes(), 
					sel.getEvery(), 
					sel.getSampling(),
					sel.getWhere(),
					sel.getExecutionConditions(),
					sel.getTerminate());
			if(sel.getEvery().getSamples()>0){
				ws = new WindowSize(sel.getEvery().getSamples()*fpcs);
				sel= new SelectionStatement(sel.getSelect(), 
						sel.getAttributes(), 
						ws, 
						sel.getSampling(),
						sel.getWhere(),
						sel.getExecutionConditions(),
						sel.getTerminate());
			}
			AggregateQueryHandler sqh = new AggregateQueryHandler(sel,h,tmp);
			query.add(sel);
			qhandler.add(sqh);
			sd = new SelectionDistributor(tmp,sqh, registry);
		}
		else{
			SelectionQueryHandler sqh = new SelectionQueryHandler(h);
			query.add(sel);
			qhandler.add(sqh);
			sd = new SelectionDistributor(sel, sqh, registry);
		}
		distributors.add(sd);
		sd.start();
	}

	private String createKey(SelectionStatement ss) {
		String key=""; 
		//Questo è per identificare una query e distringuerlo dalle altre ma va migliorato
		for(Attribute a:ss.getAttributes())
			key.concat(a.getId());
		return key;
	}

	public void compute(SelectionStatement source, Object[] value){
		Record rec=new Record(source.getAttributes(), value);		
		for(Expression e:source.getSelect().getFields()){
			if(e instanceof Aggregate){
				computeAggregate(source,value);
			}
		}
		//this.handler.data(source, rec);
		
	}
	
	private void computeAggregate(SelectionStatement source, Object value) {
		// TODO Auto-generated method stub
		
	}
	
	private void clean(int i){
		distributors.remove(i);
		qtasks.remove(i);
		qhandler.remove(i);
		handlers.remove(i);
		stasks.remove(i);
	}
	
	public class SelectionStatementTask implements StatementTask{

		@Override
		public void stop() {
			int i = stasks.indexOf(this);
			 qtasks.get(i).stop();		
		}

		@Override
		public boolean isRunning() {
			int i = stasks.indexOf(this);
			return qtasks.get(i).isRunning();
		}
		

	}
	
	
	public class SelectionQueryTask implements QueryTask{

		@Override
		public boolean isRunning() {
			int i = qtasks.indexOf(this);
			return distributors.get(i).isRunning();
		}

		@Override
		public void stop() {
			int i = qtasks.indexOf(this);
			distributors.get(i).stop();
			clean(i);
			
		}
		
	}	
	

}
