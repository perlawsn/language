package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.lang.query.expression.Expression;
import org.dei.perla.lang.query.expression.LogicValue;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * @author Guido Rota 22/10/15.
 */
public class ArrayBufferView implements BufferView {

    private final ArrayBuffer parent;
    private final CircularBuffer buffer;

    private boolean released = false;
    private int lastIdx = -1;

    private ArrayBufferView(CircularBuffer buffer) {
        this(null, buffer);
    }

    protected ArrayBufferView(
            ArrayBuffer parent,
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
            int toDelete = 0;
            if (lastIdx != -1) {
                toDelete = buffer.size() - (lastIdx + 1);
            }
            parent.release(this, toDelete);
        }
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
    public BufferView subView(int samples) {
        return new ArrayBufferView(buffer.subBuffer(samples));
    }

    @Override
    public BufferView subView(Duration d) {
        throw new RuntimeException("unimplemented");
    }

}
