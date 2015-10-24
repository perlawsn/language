package org.dei.perla.lang.executor.buffer;

import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.time.Duration;
import java.util.function.BiConsumer;

/**
 * {@link BufferView} implementation backed by a {@link CircularBuffer}
 *
 * @author Guido Rota 22/10/15.
 */
public class ArrayBufferView implements BufferView {

    private final ArrayBuffer parentBuffer;
    private final ArrayBufferView parentView;
    private final CircularBuffer buffer;

    private int subViewCount = 0;

    private boolean released = false;
    private int lastIdx = -1;

    private ArrayBufferView(
            ArrayBufferView parent,
            CircularBuffer buffer) {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null");
        }

        this.parentBuffer = null;
        this.parentView = parent;
        this.buffer = buffer;
    }

    protected ArrayBufferView(
            ArrayBuffer parent,
            CircularBuffer buffer) {
        if (parent == null) {
            throw new IllegalArgumentException("parent cannot be null");
        }

        this.parentBuffer = parent;
        this.parentView = null;
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
        } else if (subViewCount != 0) {
            throw new IllegalStateException(
                    "Sub-views have not been released"
            );
        }

        released = true;
        if (parentBuffer != null) {
            int toDelete = 0;
            if (lastIdx != -1) {
                toDelete = buffer.size() - (lastIdx + 1);
            }
            parentBuffer.release(this, toDelete);
        } else {
            parentView.releaseView();
        }
    }

    private void releaseView() {
        subViewCount--;
    }

    @Override
    public void forEach(BiConsumer<Object[], BufferView> c) {
        for (int i = 0; i < buffer.size(); i++) {
            Object[] sample = buffer.get(i);
            c.accept(sample, this);
        }
    }

    @Override
    public void forEach(
            BiConsumer<Object[], BufferView> consumer,
            Expression cond) {
        for (int i = 0; i < buffer.size(); i++) {
            Object[] sample = buffer.get(i);
            LogicValue c = (LogicValue) cond.run(sample, this);
            if (!LogicValue.toBoolean(c)) {
                continue;
            }
            consumer.accept(sample, this);
        }
    }

    @Override
    public int samplesIn(Duration d) {
        return buffer.samplesIn(d);
    }

    @Override
    public ArrayBufferView subView(int samples) {
        subViewCount++;
        return new ArrayBufferView(this, buffer.subBuffer(samples));
    }

    @Override
    public BufferView subView(Duration d) {
        return subView(samplesIn(d));
    }

}
