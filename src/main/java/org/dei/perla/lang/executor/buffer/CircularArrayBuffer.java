package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.time.Instant;
import java.util.List;

/**
 * @author Guido Rota 22/10/15.
 */
public class CircularArrayBuffer {

    public static final int DEFAULT_CAPACITY = 64;

    private final List<Attribute> atts;
    private final int tsIdx;

    private Object[][] data;
    private int head;
    private int tail;
    private int size;

    public CircularArrayBuffer(List<Attribute> atts) {
        this(atts, DEFAULT_CAPACITY);
    }

    public CircularArrayBuffer(List<Attribute> atts, int capacity) {
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
    private CircularArrayBuffer(
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

    private int previous(int pos) {
        return (data.length + pos + 1) % data.length;
    }

    protected int next(int pos) {
        return (pos + 1) % data.length;
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

    public CircularArrayBuffer createCopy() {
        return new CircularArrayBuffer(atts, tsIdx, data, head, tail, size);
    }

}
