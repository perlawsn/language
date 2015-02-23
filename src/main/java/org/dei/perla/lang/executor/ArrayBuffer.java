package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.time.Duration;
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
    private int head;
    private int tail;

    public ArrayBuffer(List<Attribute> atts, int cap) {
        tsIdx = timestampIndex(atts);
        this.atts = atts;
        this.cap = cap;
        data = new Object[cap][];
        len = 0;
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
            data[head] = r.getFields();
            len++;
            head = (head + 1) % cap;
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
                for (int i = tail, j = 0; j < len; i = (i + 1) % cap) {
                    newData[j++] = data[i];
                }
            } finally {
                dataLk.unlock();
            }
        }).run();

        cap = cap * 2;
        tail = 0;
        head = len;
    }

    @Override
    public BufferView unmodifiableView(int samples) {
        idxLk.lock();
        try {
            if (samples > len) {
                samples = len;
            }

            int newest = head - 1;
            if (newest < 0) {
                newest = cap - newest;
            }
            int oldest = head - samples;
            if (oldest < 0) {
                oldest = cap - oldest;
            }

            return new ArrayBufferView(data, newest, oldest, cap, len);
        } finally {
            idxLk.unlock();
        }
    }

    // Insertion sort, since we expect the contents of the buffer to be
    // mostly ordered for the most of the time.
    private void sort(int threshold) {
    }

    @Override
    public BufferView unmodifiableView(Duration d) {
        return null;
    }

    private class ArrayBufferView implements BufferView {

        private final Object[][] data;
        private final int cap;
        private final int len;
        private final int newest;
        private final int oldest;

        private ArrayBufferView(Object[][] data, int newest, int oldest,
                int cap, int len) {
            this.data = data;
            this.newest = newest;
            this.oldest = oldest;
            this.cap = cap;
            this.len = len;
        }

        @Override
        public List<Attribute> attributes() {
            return atts;
        }

        @Override
        public int length() {
            return len;
        }

        @Override
        public void release() {
            throw new RuntimeException("unimplemented");
        }

        @Override
        public Object[] get(int i) {
            if (i >= len) {
                throw new IndexOutOfBoundsException();
            }

            i = newest - i;
            if (i < 0) {
                i = cap - 1 - i;
            }
            if (i < oldest) {
                throw new IndexOutOfBoundsException();
            }
            return data[i];
        }

        @Override
        public BufferView view(int samples) {
            return null;
        }

        @Override
        public BufferView view(Duration d) {
            return null;
        }

    }

}
