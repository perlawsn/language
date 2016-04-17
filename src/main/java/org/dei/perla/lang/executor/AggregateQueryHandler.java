package org.dei.perla.lang.executor;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dei.perla.lang.StatementHandler;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.executor.buffer.UnreleasedViewException;
import org.dei.perla.lang.executor.statement.QueryHandler;
import org.dei.perla.lang.query.expression.LogicValue;
import org.dei.perla.lang.query.statement.Sampling;
import org.dei.perla.lang.query.statement.SelectionStatement;
import org.dei.perla.lang.query.statement.WindowSize;

public class AggregateQueryHandler extends SelectionQueryHandler{
	private int fpcs,current;
	private final Buffer buffer;
	private SelectionStatement sel;
	private int everyCount;
	private static final ScheduledExecutorService timer =
            Executors.newScheduledThreadPool(1);
    private static final ExecutorService exec =
            Executors.newCachedThreadPool();
    private ScheduledFuture<?> everyThread;
    private StatementHandler h;
    private final Lock lk = new ReentrantLock();
    
	public AggregateQueryHandler(SelectionStatement sel, StatementHandler h, SelectionStatement fake) {
		super(h);
		this.sel=sel;
		this.h = h;
		buffer = new ArrayBuffer(fake.getAttributes());
		startEvery();
	}

	@Override
	public void error(SelectionStatement source, Throwable cause) {
		System.out.println(source.toString());
		System.out.println(cause.getMessage());		
	}

	@Override
	public void data(SelectionStatement source, Object[] value) {
		 lk.lock();
         try {
        	 Object[] o = new Object[value.length+1];
        	 int i;
        	 for(i=0;i<value.length;i++)
        		 o[i]=value[i];
        	 o[value.length]=java.time.Instant.now();
             buffer.add(o);
             if (sel.getEvery().getType() == WindowSize.WindowType.SAMPLE) {
             	triggerCountSampling();
             }
         } finally {
             lk.unlock();
         }
	}

    private void triggerCountSampling() {
        everyCount--;
        if (everyCount != 0) {
            return;
        }

        try {
            everyCount = sel.getEvery().getSamples();
            BufferView view = buffer.createView();
            exec.execute(new SelectionRunner(view));
        } catch(UnreleasedViewException e) {
            h.error(sel, e);
        }
    }

	public void complete() {
		super.getHandler().complete();
	}
   
	/**
     * Every runner
     */
    private final class EveryRunner implements Runnable {

        public void run() {
            lk.lock();
            try {
                BufferView view = buffer.createView();
                exec.execute(new SelectionRunner(view));
            } catch (UnreleasedViewException e) {
            	h.error(sel, e);
            } finally {
                lk.unlock();
            }
        }

    }
	private void startEvery() {
        switch (sel.getEvery().getType()) {
            case TIME:
                long ms = sel.getEvery().getDuration().toMillis();
                everyThread = timer.scheduleAtFixedRate(
                        new EveryRunner(),
                        ms,
                        ms,
                        TimeUnit.MILLISECONDS);
                break;
            case SAMPLE:
                everyCount = sel.getEvery().getSamples();
                break;
            default:
                throw new RuntimeException(
                        "Unknown window size type " +  sel.getEvery().getType());
        }
	}
        
        private final class SelectionRunner implements Runnable {

            private final BufferView view;

            public SelectionRunner(BufferView view) {
                this.view = view;
            }

            public void run() {
                List<Object[]> res = sel.select(view);
                Record r;
                view.release();
                lk.lock();
                float f=0;
                try {
                    for(Object[] value:res){
            		r= new Record(sel.getAttributes(), value);
            	//	System.out.println("prova");
            		try{
            			String s;
            			for(int i=0;i<r.getValues().length;i++)
            				 System.out.println(r.getFields().get(i).getId()+" "+r.getValues()[i]);	
            		h.data(sel,r);
            		}
            		catch(Exception e){
            			System.out.println(e.toString());
            		}
                    }
                } finally {
                    lk.unlock();
                }
            }

        }
    }
	





