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

    private final Lock idxLk = new ReentrantLock();
    private final Lock dataLk = new ReentrantLock();
    private volatile Object[][] data;
    private int cap;
    private int len;
    private int head;
    private int tail;

    public ArrayBuffer(List<Attribute> atts, int cap) {
        this.atts = atts;
        this.cap = cap;
        data = new Object[cap][];
        len = 0;
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

        // copies the old data using a different thread, so that the calling
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
    public BufferView view() {
        idxLk.lock();
        dataLk.lock();
        try {
            if (len == 0) {
                return new EmptyBufferView();
            } else {
                return new ArrayBufferView(data, head - 1, tail, cap, len);
            }
        } finally {
            dataLk.unlock();
            idxLk.unlock();
        }
    }

    @Override
    public BufferView range(int samples) {
        return null;
    }

    @Override
    public BufferView range(Duration d) {
        return null;
    }

    private class EmptyBufferView implements BufferView {

        @Override
        public List<Attribute> attributes() {
            return atts;
        }

        @Override
        public int length() {
            return 0;
        }

        @Override
        public Object[] get(int i) {
            throw new IndexOutOfBoundsException();
        }

        @Override
        public BufferView range(int samples) {
            return new EmptyBufferView();
        }

        @Override
        public BufferView range(Duration d) {
            return new EmptyBufferView();
        }
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
        public BufferView range(int samples) {
            return null;
        }

        @Override
        public BufferView range(Duration d) {
            return null;
        }

    }

}
