package org.dei.perla.lang.executor.buffer;

/**
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBufferView implements NewBufferView {

    private final NewArrayBuffer parent;
    private final CircularBuffer buffer;

    private boolean released = false;
    private int lastIdx = -1;

    protected NewArrayBufferView(
            NewArrayBuffer parent,
            CircularBuffer buffer) {
        this.parent = parent;
        this.buffer = buffer;
    }

    public int size() {
        if (released) {
            throw new IllegalStateException(
                    "Cannot access buffer view after release"
            );
        }
        return buffer.size();
    }

    @Override
    public Object[] get(int i) {
        if (released) {
            throw new IllegalStateException(
                    "Cannot access buffer view after release"
            );
        }

        if (i > lastIdx) {
            lastIdx = i;
        }
        return buffer.get(i);
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
            int toDelete = buffer.size() - lastIdx + 1;
            parent.release(this, toDelete);
        }
    }

}
