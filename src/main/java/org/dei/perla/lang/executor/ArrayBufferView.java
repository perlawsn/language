package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Expression;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * @author Guido Rota 26/02/15.
 */
public final class ArrayBufferView extends ArrayBufferReleaser
        implements BufferView {

    private final ArrayBufferReleaser parent;
    private final List<Attribute> atts;
    private final int tsIdx;
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
        tsIdx = parent.getTimestampIndex();
        this.length = length;
        oldest = 0;
        newest = length - 1;
        released = false;
    }

    protected ArrayBufferView(ArrayBufferView parent, int oldest, int newest) {
        this.parent = parent;
        this.data = parent.data;
        atts = parent.attributes();
        tsIdx = parent.getTimestampIndex();
        this.length = newest - oldest;
        this.newest = newest;
        this.oldest = oldest;
    }

    protected int getTimestampIndex() {
        return tsIdx;
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
            parent.releaseChildView(this);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void releaseChildView(BufferView v) {
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
    public void forEach(BiConsumer<Object[], BufferView> c) {
        for (int i = oldest; i <= newest; i++) {
            c.accept(data[i], this);
        }
    }

    @Override
    public void forEach(BiConsumer<Object[], BufferView> c, Expression e) {
        for (int i = oldest; i <= newest; i++) {
            Boolean cond = (Boolean) e.compute(data[i], this);
            if (!cond) {
                continue;
            }
            c.accept(data[i], this);
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

    @Override
    public BufferView subView(Duration d) {
        lock.lock();
        try {
            int i = 0;
            int top = newest;
            int bottom = oldest;
            Instant target = timestamp(newest).minus(d);

            while (top >= bottom) {
                i = bottom + (top - bottom) / 2;
                int c = timestamp(i).compareTo(target);
                if (c == 0) {
                    break;
                } else if (c > 0) {
                    top = i - 1;
                } else {
                    bottom = i + 1;
                }
            }

            return subView(length - i);
        } finally {
            lock.unlock();
        }
    }

    private Instant timestamp(int i) {
        return (Instant) data[i][tsIdx];
    }

}
