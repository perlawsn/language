package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.StatementHandler;
import org.dei.perla.lang.query.statement.Statement;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 14/05/15.
 */
public class LatchingStatementHandler<E extends Statement>
        implements StatementHandler<E> {

    private int waitCount;
    private Throwable error;

    private int dataCount = 0;

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();

    public LatchingStatementHandler(int count) {
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
    public void complete(E source) {
        lk.lock();
        try {
            if (waitCount == 0) {
                return;
            }

            error = new RuntimeException("premature termination");
            waitCount = 0;
            cond.signalAll();
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
    public void data(E source, Object[] record) {
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
