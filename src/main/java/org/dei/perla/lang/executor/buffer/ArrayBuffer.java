package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A sample buffer backed by a circular array
 *
 * @author Guido Rota 22/10/15.
 */
public class ArrayBuffer implements Buffer {

    private final Lock dataLk = new ReentrantLock();
    private boolean viewActive = false;

    private CircularBuffer buffer;

    /**
     * Creates a new buffer backed by a circular array
     */
    public ArrayBuffer(List<Attribute> atts) {
        buffer = new CircularBuffer(atts);
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
    public ArrayBufferView createView() throws UnreleasedViewException {
        dataLk.lock();
        try {
            if (viewActive) {
                throw new UnreleasedViewException();
            }
            viewActive = true;
            return new ArrayBufferView(this, buffer.createCopy());
        } finally {
            dataLk.unlock();
        }
    }

    protected void release(ArrayBufferView view, int toDelete) {
        dataLk.lock();
        try {
            buffer.deleteLast(toDelete);
            viewActive = false;
        } finally {
            dataLk.unlock();
        }
    }

}
