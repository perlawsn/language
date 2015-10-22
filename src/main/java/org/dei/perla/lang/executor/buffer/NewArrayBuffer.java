package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A sample buffer backed by a circular array
 *
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBuffer implements NewBuffer {

    private final Lock dataLk = new ReentrantLock();
    private final Semaphore viewSem = new Semaphore(1);
    private NewArrayBufferView view;

    private CircularArrayBuffer buffer;

    /**
     * Creates a new buffere backed by a circular array
     */
    public NewArrayBuffer(List<Attribute> atts) {
        buffer = new CircularArrayBuffer(atts);
    }

    /**
     * Creates a new buffer backed by a circular array with user-defined
     * initial capacity
     *
     * @param capacity initial capacity
     */
    public NewArrayBuffer(List<Attribute> atts, int capacity) {
        buffer = new CircularArrayBuffer(atts, capacity);
    }

    @Override
    public List<Attribute> getAttributes() {
        return buffer.getAttributes();
    }

    public int size() {
        dataLk.lock();
        try {
            return buffer.size();
        } finally {
            dataLk.unlock();
        }
    }

    public int capacity() {
        dataLk.lock();
        try {
            return buffer.capacity();
        } finally {
            dataLk.unlock();
        }
    }

    @Override
    public void add(Object[] sample) {
        dataLk.lock();
        try {
            buffer.add(sample);
        } finally {
            dataLk.unlock();
        }
    }

    @Override
    public NewBufferView createView() throws InterruptedException {
        NewArrayBufferView newView;
        dataLk.lock();
        try {
            newView = new NewArrayBufferView(this, buffer.createCopy());
        } finally {
            dataLk.unlock();
        }
        viewSem.acquire();
        this.view = newView;
        return newView;
    }

    protected void release(NewArrayBufferView view, int lastIdx) {
        if (this.view != view) {
            throw new IllegalStateException(
                    "The view being released is not current"
            );
        }
        this.view = null;
        viewSem.release();
    }

}
