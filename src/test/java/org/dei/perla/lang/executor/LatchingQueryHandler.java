package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.statement.QueryHandler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 13/04/15.
 */
public class LatchingQueryHandler<E, T>
        implements QueryHandler<E, T> {

    private Throwable error;
    private boolean complete = false;
    private int dataCount = 0;

    private final Lock lk = new ReentrantLock();
    private final Condition cond = lk.newCondition();

    public void awaitCount(int count) throws InterruptedException {
        lk.lock();
        try {
            while (dataCount < count && error == null) {
                cond.await();
            }
            if (error != null) {
                throw new RuntimeException(error);
            }
        } finally {
            lk.unlock();
        }
    }

    public void awaitComplete() throws InterruptedException {
        lk.lock();
        try {
            while (!complete && error == null) {
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
            complete = true;
            cond.signalAll();
        } finally {
            lk.unlock();
        }
    }

    @Override
    public void error(E source, Throwable error) {
        lk.lock();
        try {
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
            cond.signalAll();
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
