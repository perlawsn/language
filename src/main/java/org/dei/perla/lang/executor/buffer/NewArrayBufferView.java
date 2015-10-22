package org.dei.perla.lang.executor.buffer;

/**
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBufferView implements NewBufferView {

    private final NewArrayBuffer parent;
    private final Object[][] data;
    private final int head;
    private final int tail;
    private final int size;

    private boolean released = false;
    private int lastIdx = 0;

    protected NewArrayBufferView(
            NewArrayBuffer parent,
            Object[][] data,
            int head,
            int tail,
            int size) {
        this.parent = parent;
        this.data = data;
        this.head = head;
        this.tail = tail;
        this.size = size;
    }

    public int size() {
        if (released) {
            throw new IllegalStateException(
                    "Cannot access buffer view after release"
            );
        }
        return size;
    }

    @Override
    public Object[] get(int i) {
        if (released) {
            throw new IllegalStateException(
                    "Cannot access buffer view after release"
            );
        }

        if (i >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (i > lastIdx) {
            lastIdx = i;
        }

        int idx = head - i;
        if (idx < 0) {
            idx = data.length - idx;
        }
        return data[idx];
    }

    @Override
    public void release() {
        if (released) {
            throw new IllegalStateException(
                    "Cannot access buffer view after release"
            );
        }

        released = true;
        if (parent != null) {
            parent.release(this, lastIdx);
        }
    }

}
