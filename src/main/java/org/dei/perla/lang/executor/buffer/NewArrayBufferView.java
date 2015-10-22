package org.dei.perla.lang.executor.buffer;

/**
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBufferView implements NewBufferView {

    private final Object[][] data;
    private final int head;
    private final int tail;
    private final int size;

    protected NewArrayBufferView(
            Object[][] data,
            int head,
            int tail,
            int size) {
        this.data = data;
        this.head = head;
        this.tail = tail;
        this.size = size;
    }

    public int size() {
        return size;
    }

    @Override
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

}
