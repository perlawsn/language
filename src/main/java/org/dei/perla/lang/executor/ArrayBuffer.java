package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Guido Rota 22/02/15.
 */
public final class ArrayBuffer implements Buffer {

    private final List<Attribute> atts;

    // Index of the timestamp attribute
    private final int tsIdx;

    private final Lock idxLk = new ReentrantLock();
    private final Lock dataLk = new ReentrantLock();
    private Object[][] data;
    private int cap;
    private int len;

    private boolean hasView = false;

    public ArrayBuffer(List<Attribute> atts, int cap) {
        tsIdx = timestampIndex(atts);
        this.atts = atts;
        data = new Object[cap][];
        this.cap = cap;
        this.len = 0;
    }

    // Retrieves the column index of the timestamp attribute
    private int timestampIndex(List<Attribute> atts) {
        int i = 0;
        for (Attribute a : atts) {
            if (a == Attribute.TIMESTAMP_ATTRIBUTE) {
                return i;
            }
        }
        throw new IllegalArgumentException(
                "missing timestamp attribute in attribute list");
    }

    @Override
    public List<Attribute> attributes() {
        return atts;
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
    public void add(Record r) {
        idxLk.lock();
        try {
            if (len == cap) {
                grow();
            }
            data[len] = r.getFields();
            len++;
        } finally {
            idxLk.unlock();
        }
    }

    public void grow() {
        Object[][] newData = new Object[cap * 2][];

        // Copies the old data using a different thread, so that the calling
        // thread can continue with adding new records to the head of the array.
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
        } finally {
            idxLk.unlock();
            dataLk.unlock();
        }

        sort(array, threshold);
        return new ArrayBufferView(array, threshold - 1, 0);
    }

    // Insertion sort, since we expect the content of the buffer to be
    // in chronological ordered for most of the time.
    private void sort(Object[][] ar, int threshold) {
        Instant in;
        int j;

        for (int i = 1; i < threshold; i++) {
            j = i - 1;
            in = (Instant)ar[i][tsIdx];
            while (j >= 0 && in.compareTo((Instant)ar[j][tsIdx]) < 0) {
                Object[] tmp = ar[i];
                ar[i] = ar[j];
                ar[j] = tmp;
                j--;
            }
        }
    }

    private class ArrayBufferView implements BufferView {

        private final Object[][] data;
        private final int newest;
        private final int oldest;

        private ArrayBufferView(Object[][] data, int newest, int oldest) {
            this.data = data;
            this.newest = newest;
            this.oldest = oldest;
        }

        @Override
        public List<Attribute> attributes() {
            return atts;
        }

        @Override
        public int length() {
            return oldest - newest + 1;
        }

        @Override
        public void release() {
            idxLk.lock();
            try {
                hasView = false;
            } finally {
                idxLk.unlock();
            }
        }

        @Override
        public Object[] get(int i) {
            if (i > oldest - newest + 1) {
                throw new IndexOutOfBoundsException();
            }
            return data[newest - i];
        }

    }

}
