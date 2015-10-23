package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A sample buffer backed by a circular array
 *
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBuffer implements NewBuffer {

    private final Lock dataLk = new ReentrantLock();
    private Queue<NewBufferView> views = new LinkedList<>();

    private CircularBuffer buffer;

    /**
     * Creates a new buffer backed by a circular array
     */
    public NewArrayBuffer(List<Attribute> atts) {
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
    public NewArrayBufferView createView() {
        dataLk.lock();
        try {
            NewArrayBufferView newView =
                    new NewArrayBufferView(this, buffer.createCopy());
            views.add(newView);
            return newView;
        } finally {
            dataLk.unlock();
        }
    }

    protected void release(NewArrayBufferView view, int toDelete) {
        dataLk.lock();
        try {
            NewBufferView storedView = views.poll();
            if (storedView == view) {
                buffer.deleteLast(toDelete);
            }
        } finally {
            dataLk.unlock();
        }
    }

}
