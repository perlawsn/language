package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
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
        tsIdx = parent.getTimestampIndex();
        this.length = length;
        oldest = 0;
        newest = length - 1;
        released = false;
    }

    protected ArrayBufferView(ArrayBufferView parent, int oldest, int newest) {
        this.parent = parent;
        this.data = parent.data;
        tsIdx = parent.getTimestampIndex();
        this.length = newest - oldest + 1;
        this.newest = newest;
        this.oldest = oldest;
    }

    /**
     * {@code getTimestampIndex} returns the index of the column which
     * contains the record timestamp.
     */
    protected int getTimestampIndex() {
        return tsIdx;
    }

    /**
     * {@code timestamp} returns the timestamp associated with the ith record
     * in the buffer.
     */
    private Instant timestamp(int i) {
        return (Instant) data[i][tsIdx];
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
        if (e == null) {
            forEach(c);
            return;
        }

        for (int i = oldest; i <= newest; i++) {
            Boolean cond = (Boolean) e.run(data[i], this);
            if (!cond) {
                continue;
            }
            c.accept(data[i], this);
        }
    }

    @Override
    public int recordsIn(Duration d) {
        lock.lock();
        try {
            return count(d);
        } finally {
            lock.unlock();
        }
    }

    /**
     * {@code count} counts the number of records whose timestamp is greater
     * or equal than (newest - d).
     */
    private int count(Duration d) {
        int i = 0;
        int top = newest;
        int bottom = oldest;
        Instant target = timestamp(newest).minus(d);

        while (top > bottom) {
            i = bottom + (top - bottom) / 2;
            int c = timestamp(i).compareTo(target);
            if (c == 0) {
                break;
            } else if (c > 0) {
                top = i;
            } else {
                bottom = i + 1;
            }
        }
        if (top == bottom) {
            i = top;
        }
        return length - i;
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
                    new ArrayBufferView(this, newest + 1 - samples, newest);
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
            return subView(count(d));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<BufferView> groupBy(Duration d, int count) {
        lock.lock();
        try {
            List<BufferView> bfs = new LinkedList<>();

            // The first timestamped-grouped buffer always contains all records
            ArrayBufferView v = new ArrayBufferView(this, oldest, newest);
            bfs.add(v);
            views.add(v);

            int lastIdx = length - 1;
            for (int i = 1; i < count; i++) {
                Duration cd = d.multipliedBy(i);
                int idx = length - count(cd) - 1;
                if (idx >= lastIdx && idx > oldest) {
                    idx = lastIdx - 1;
                } else if (idx < oldest) {
                    idx = oldest;
                }
                if (timestamp(lastIdx).minus(d).compareTo(timestamp(idx)) < 0) {
                    break;
                }
                v = new ArrayBufferView(this, oldest, idx);
                bfs.add(v);
                views.add(v); // needed for correct release of views
                lastIdx = idx;
            }
            return bfs;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<BufferView> groupBy(List<Expression> fields) {
        throw new RuntimeException("unimplemented");
    }

}
