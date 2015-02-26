package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 26/02/15.
 */
public class ArrayBufferView extends ArrayBufferReleaser implements BufferView {

    private final ArrayBufferReleaser parent;
    private final List<Attribute> atts;
    private final Object[][] data;
    private final int oldest;
    private final int newest;
    private final int length;

    private final Lock lock = new ReentrantLock();
    private final List<ArrayBufferView> views = new ArrayList<>();
    private boolean released;

    protected ArrayBufferView(ArrayBuffer parent, Object[][] data, int length) {
        this.parent = parent;
        this.data = data;
        atts = parent.attributes();
        this.length = length;
        oldest = 0;
        newest = length - 1;
        released = false;
    }

    protected ArrayBufferView(ArrayBufferView parent, int oldest, int newest) {
        this.parent = parent;
        this.data = parent.data;
        atts = parent.attributes();
        this.length = newest - oldest + 1;
        this.newest = newest;
        this.oldest = oldest;
    }

    @Override
    public List<Attribute> attributes() {
        return atts;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public void release() {
        lock.lock();
        try {
            if (!views.isEmpty()) {
                throw new IllegalStateException("unreleased sub-views exist");
            }
            released = true;
            parent.releaseView(this);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void releaseView(BufferView v) {
        lock.lock();
        try {
            views.remove(v);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Object[] get(int i) {
        lock.lock();
        try {
            if (released) {
                throw new IllegalStateException("view was released");
            }
            if (i > length) {
                throw new IndexOutOfBoundsException();
            }
            return data[newest - i];
        } finally {
            lock.unlock();
        }
    }

    @Override
    public BufferView subView(int samples) {
        lock.lock();
        try {
            if (released) {
                throw new IllegalStateException("view was released");
            }
            if (samples > length) {
                samples = length;
            }
            ArrayBufferView v =
                    new ArrayBufferView(this, newest - samples, newest);
            views.add(v);
            return v;
        } finally {
            lock.unlock();
        }
    }

}
