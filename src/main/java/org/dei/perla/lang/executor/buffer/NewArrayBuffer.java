package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.time.Instant;
import java.util.List;

/**
 * A sample buffer backed by a circular array
 *
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBuffer implements NewBuffer {

    public static final int DEFAULT_CAPACITY = 64;

    private final List<Attribute> atts;
    private final int tsIdx;

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
        return size;
    }

    public int capacity() {
        return data.length;
    }

    protected int previous(int pos) {
        if (pos == 0) {
            return data.length;
        } else {
            return pos - 1;
        }
    }

    protected int next(int pos) {
        return (pos + 1) % data.length;
    }

    @Override
    public void add(Object[] sample) {
        if (sample == null || sample.length != atts.size() ||
                sample[tsIdx] == null) {
            throw new RuntimeException("Malformed sample");
        }

        if (size == data.length) {
            expand();
        }
        head = next(head);
        data[head] = sample;
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
        head = size;
        data = newData;
    }

    /**
     * Insertion sort. Samples are expected to arrive pretty much in order,
     * hence the decision to use insertion sort.
     *
     * @param sample new sample to insert
     */
    private void insertionSort(Object[] sample) {
        int pos = next(head);
        int prev = head;
        Instant sampleTs = (Instant) sample[tsIdx];
        Instant prevTs = (Instant) data[prev][tsIdx];
        while (size != 0 && pos != tail &&
                sampleTs.compareTo(prevTs) < 0) {
            data[pos] = data[prev];
            pos = prev;
            prev = previous(prev);
            prevTs = (Instant) data[prev][tsIdx];
        }
        data[pos] = sample;
    }

}
