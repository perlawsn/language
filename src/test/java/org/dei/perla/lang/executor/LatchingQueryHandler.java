package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.Clause;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 13/04/15.
 */
public class LatchingQueryHandler<E extends Clause, T>
        implements QueryHandler<E, T> {

    private int waitCount;
    private Throwable error;

    private int dataCount = 0;

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();

    public LatchingQueryHandler(int count) {
        this.waitCount = count;
    }

    public void await() throws InterruptedException {
        lk.lock();
        try {
            while(waitCount != 0) {
                cond.await();
            }
            if (error != null) {
                throw new RuntimeException(error);
            }
        } finally {
            lk.unlock();
        }
    }

    @Override
    public void error(E source, Throwable error) {
        lk.lock();
        try {
            waitCount = 0;
            this.error = error;
            cond.signalAll();
        } finally {
            lk.unlock();
        }
    }

    @Override
    public void data(E source, T value) {
        lk.lock();
        try {
            dataCount++;
            if (waitCount == 0) {
                return;
            }

            waitCount--;
            if (waitCount == 0) {
                cond.signalAll();
            }
        } finally {
            lk.unlock();
        }
    }

    public int getDataCount() {
        lk.lock();
        try {
            return dataCount;
        } finally {
            lk.unlock();
        }
    }

}
