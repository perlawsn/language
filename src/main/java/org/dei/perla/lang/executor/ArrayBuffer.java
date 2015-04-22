package org.dei.perla.lang.executor;

import org.dei.perla.core.sample.Attribute;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 22/02/15.
 */
public final class ArrayBuffer extends ArrayBufferReleaser implements Buffer {

    // Index of the timestamp attribute
    private final int tsIdx;

    private final Lock dataLk = new ReentrantLock();
    private Object[][] data;

    private final Lock idxLk = new ReentrantLock();
    private int cap;
    private int len;
    private boolean hasView = false;

    public ArrayBuffer(List<Attribute> atts, int cap) {
        data = new Object[cap][];
        this.tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx < 0) {
            throw new IllegalArgumentException("Missing timestamp attribute");
        }
        this.cap = cap;
        this.len = 0;
    }

    protected int getTimestampIndex() {
        return tsIdx;
    }

    public int length() {
        idxLk.lock();
        try {
            return len;
        } finally {
            idxLk.unlock();
        }
    }

    @Override
    public void add(Object[] s) {
        idxLk.lock();
        try {
            if (len == cap) {
                grow();
            }
            data[len] = s;
            len++;
        } finally {
            idxLk.unlock();
        }
    }

    public void grow() {
        Object[][] newData = new Object[cap * 2][];

        // Copies the old data using a different thread, so that the calling
        // thread can continue with adding new samples to the head of the array.
        new Thread(() -> {
            dataLk.lock();
            try {
                for (int i = 0; i < len; i++) {
                    newData[i] = data[i];
                }
            } finally {
                dataLk.unlock();
            }
        }).run();

        cap = cap * 2;
        data = newData;
    }

    @Override
    public BufferView unmodifiableView() {
        Object[][] array;
        int threshold;

        idxLk.lock();
        dataLk.lock();
        try {
            if (hasView) {
                throw new IllegalStateException("cannot create a new view " +
                        "before releasing the old one");
            }
            array = data;
            threshold = len;
            hasView = true;
        } finally {
            idxLk.unlock();
            dataLk.unlock();
        }

        sort(array, threshold);
        return new ArrayBufferView(this, array, threshold);
    }

    // Insertion sort, since we expect the content of the buffer to be
    // in chronological ordered for most of the time.
    private void sort(Object[][] ar, int threshold) {
        int j;

        for (int i = 1; i < threshold; i++) {
            j = i;
            while (j > 0 && ((Instant) ar[j][tsIdx])
                    .compareTo((Instant) ar[j - 1][tsIdx]) < 0) {
                Object[] tmp = ar[j];
                ar[j] = ar[j-1];
                ar[j-1] = tmp;
                j--;
            }
        }
    }

    @Override
    protected void releaseChildView(BufferView v) {
        idxLk.lock();
        try {
            hasView = false;
        } finally {
            idxLk.unlock();
        }
    }

}
