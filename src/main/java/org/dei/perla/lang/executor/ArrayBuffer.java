package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Guido Rota 22/02/15.
 */
public final class ArrayBuffer implements Buffer {

    private final List<Attribute> atts;

    private ReadWriteLock lk = new ReentrantReadWriteLock();
    private Object[][] data;
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

    @Override
    public void add(Record r) {
        lk.writeLock().lock();
        try {
            if (len == cap) {
                grow();
            }
            data[head] = r.getFields();
            len++;
            head++;
            if (head > cap - 1) {
                head = 0;
            }
        } finally {
            lk.writeLock().unlock();
        }
    }

    public void grow() {
    }

    @Override
    public BufferView range(int samples) {
        return null;
    }

    @Override
    public BufferView range(Duration d) {
        return null;
    }

    public static class ArrayBufferView implements BufferView {

        private final Object[][] data;
        private final int head;
        private final int tail;

        private ArrayBufferView(Object[][] data, int head, int tail) {
            this.data = data;
            this.head = head;
            this.tail = tail;
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
