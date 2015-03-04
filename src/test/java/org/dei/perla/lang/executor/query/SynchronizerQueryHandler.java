package org.dei.perla.lang.executor.query;

import org.dei.perla.lang.executor.QueryHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 03/03/15.
 */
public class SynchronizerQueryHandler implements QueryHandler {

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();
    private int upto;
    private final List<Object[]> records = new ArrayList<>();

    public SynchronizerQueryHandler(int upto) {
        this.upto = upto;
    }

    @Override
    public void newRecord(Object[] r) {
        lk.lock();
        try {
            if (upto != 0) {
                records.add(r);
                upto--;
                cond.signalAll();
            }
        } finally {
            lk.unlock();
        }
    }

    public List<Object[]> getRecords() throws InterruptedException {
        lk.lock();
        try {
            while (upto != 0) {
                cond.await();
            }
            return records;
        } finally {
            lk.unlock();
        }
    }

}
