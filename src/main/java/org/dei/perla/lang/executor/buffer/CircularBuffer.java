package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

/**
 * @author Guido Rota 22/10/15.
 */
public class CircularBuffer {

    public static final int DEFAULT_CAPACITY = 64;

    private final List<Attribute> atts;
    private final int tsIdx;

    private Object[][] data;
    private int head;
    private int tail;
    private int size;

    public CircularBuffer(List<Attribute> atts) {
        this(atts, DEFAULT_CAPACITY);
    }

    public CircularBuffer(List<Attribute> atts, int capacity) {
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

    // Copy constructor
    private CircularBuffer(
            List<Attribute> atts,
            int tsIdx,
            Object[][] data,
            int head,
            int tail,
            int size) {
        this.atts = atts;
        this.tsIdx = tsIdx;
        this.data = data;
        this.head = head;
        this.tail = tail;
        this.size = size;
    }

    public List<Attribute> getAttributes() {
        return atts;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return data.length;
    }

    private int previous(int idx) {
        return (data.length + idx - 1) % data.length;
    }

    protected int advance(int idx, int count) {
        return (idx + count) % data.length;
    }

    protected int next(int idx) {
        return advance(idx, 1);
    }

    public Object[] get(int i) {
        if (i >= size) {
            throw new IndexOutOfBoundsException();
        }

        int idx = head - i;
        if (idx < 0) {
            idx = data.length - idx;
        }
        return data[idx];
    }

    public void add(Object[] sample) {
        if (sample == null || sample.length != atts.size() ||
                sample[tsIdx] == null) {
            throw new RuntimeException("Malformed sample");
        }

        if (size == data.length) {
            expand();
        }
        insertSample(sample);
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
        Instant prevTs = timestamp(prev);
        while (pos != tail && sampleTs.compareTo(timestamp(prev)) < 0) {
            data[pos] = data[prev];
            pos = prev;
            prev = previous(prev);
        }
        data[pos] = sample;
        size++;
    }

    private Instant timestamp(int idx) {
        return (Instant) data[idx][tsIdx];
    }

    public void deleteLast(int count) {
        if (count > size) {
            throw new IndexOutOfBoundsException("Not enough elements");
        }

        // Just clear the buffer when all data must be deleted
        if (count == size) {
            clear();
            return;
        }

        tail = advance(tail, count);
        size -= count;
    }

    public void clear() {
        head = 0;
        tail = 0;
        size = 0;
    }

    public CircularBuffer createCopy() {
        return new CircularBuffer(atts, tsIdx, data, head, tail, size);
    }

    public CircularBuffer subBuffer(int count) {
        if (count > data.length) {
            throw new IndexOutOfBoundsException("Not enough elements");
        } else if (count == 0) {
            return newEmptyBuffer();
        }

        int newTail = advance(tail, data.length - count);
        return new CircularBuffer(atts, tsIdx, data, head, newTail, count);
    }

    public CircularBuffer subBuffer(Duration d) {
        throw new RuntimeException("unimplemented");
    }

    public int samplesIn(Duration d) {
        Instant target = timestamp(head).minus(d);
        int i = 0;

        int pos = head;
        int count = 0;
        while (count < size && timestamp(pos).compareTo(target) > 0) {
            pos = previous(pos);
            count++;
        }
        return count;
    }

    private CircularBuffer newEmptyBuffer() {
        return new CircularBuffer(atts, tsIdx, null, 0, 0, 0);
    }

}
