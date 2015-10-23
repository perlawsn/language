package org.dei.perla.lang.executor.buffer;

import org.dei.perla.lang.query.expression.Expression;

import java.time.Duration;
import java.util.function.BiConsumer;

/**
 * An unmodifiable view over the content of a {@link Buffer}
 *
 * @author Guido Rota 22/10/15.
 */
public interface BufferView {

    public int size();

    public Object[] get(int i);

    public void release();

    public void forEach(BiConsumer<Object[], BufferView> c);

    public void forEach(BiConsumer<Object[], BufferView> c, Expression cond);

    public int samplesIn(Duration d);

    public BufferView subView(int samples);

    public BufferView subView(Duration d);

}
