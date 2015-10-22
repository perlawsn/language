package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A sample buffer backed by a circular array
 *
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBuffer implements NewBuffer {

    public static final int DEFAULT_CAPACITY = 64;

    private final List<Attribute> atts;
    private final int tsIdx;

    private final Lock dataLk = new ReentrantLock();
    private final Semaphore viewSem = new Semaphore(1);
    private NewArrayBufferView view;

    private Object[][] data;
    private int head;
    private int tail;
    private int size;

    /**
     * Creates a new buffere backed by a circular array
     */
    public NewArrayBuffer(List<Attribute> atts) {
        this(atts, DEFAULT_CAPACITY);
    }

    /**
     * Creates a new buffer backed by a circular array with user-defined
     * initial capacity
     *
     * @param capacity initial capacity
     */
    public NewArrayBuffer(List<Attribute> atts, int capacity) {
        this.atts = atts;
        data = new Object[capacity][];
        head = 0;
        tail = 0;
        size = 0;
        tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx == -1) {
            throw new IllegalArgumentException(
                    "Missing timestamp attribute in sample fields"
            );
        }
    }

    @Override
    public List<Attribute> getAttributes() {
        return atts;
    }

    public int size() {
        dataLk.lock();
        try {
            return size;
        } finally {
            dataLk.unlock();
        }
    }

    public int capacity() {
        dataLk.lock();
        try {
            return data.length;
        } finally {
            dataLk.unlock();
        }
    }

    protected static int previous(int capacity, int pos) {
        return (capacity + pos + 1) % capacity;
    }

    protected int previous(int pos) {
        return NewArrayBuffer.previous(data.length, pos);
    }

    protected static int next(int capacity, int pos) {
        return (pos + 1) % capacity;
    }

    protected int next(int pos) {
        return NewArrayBuffer.next(data.length, pos);
    }

    @Override
    public void add(Object[] sample) {
        dataLk.lock();
        try {
            if (sample == null || sample.length != atts.size() ||
                    sample[tsIdx] == null) {
                throw new RuntimeException("Malformed sample");
            }

            if (size == data.length) {
                expand();
            }
            insertSample(sample);
        } finally {
            dataLk.unlock();
        }
    }

    /**
     * Expands the circular array's size
     */
    private void expand() {
        int newSize = data.length * 2;
        Object[][] newData = new Object[newSize][];

        if (head > tail) {
            System.arraycopy(
                    data,
                    tail,
                    newData,
                    0,
                    size
            );
        } else {
            int copyLen = data.length - tail + 1;
            System.arraycopy(
                    data,
                    tail,
                    newData,
                    0,
                    copyLen
            );
            System.arraycopy(
                    data,
                    0,
                    newData,
                    copyLen,
                    head + 1
            );
        }

        tail = 0;
        head = size - 1;
        data = newData;
    }

    /**
     * Insertion sort. Samples are expected to arrive pretty much in order,
     * hence the decision to use insertion sort.
     *
     * @param sample new sample to insert
     */
    private void insertSample(Object[] sample) {
        if (size == 0) {
            data[head] = sample;
            size++;
            return;
        }

        int prev = head;
        head = next(head);
        int pos = head;
        Instant sampleTs = (Instant) sample[tsIdx];
        Instant prevTs = (Instant) data[prev][tsIdx];
        while (size != 0 && prev != tail &&
                sampleTs.compareTo(prevTs) < 0) {
            data[pos] = data[prev];
            pos = prev;
            prev = previous(prev);
            prevTs = (Instant) data[prev][tsIdx];
        }
        data[pos] = sample;
        size++;
    }

    @Override
    public NewBufferView createView() throws InterruptedException {
        NewArrayBufferView newView;
        dataLk.lock();
        try {
            newView = new NewArrayBufferView(this, data, head, tail, size);
        } finally {
            dataLk.unlock();
        }
        viewSem.acquire();
        this.view = newView;
        return newView;
    }

    protected void release(NewArrayBufferView view, int lastIdx) {
        if (this.view != view) {
            throw new IllegalStateException(
                    "The view being released is not current"
            );
        }
        this.view = null;
        viewSem.release();
    }

}
