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

    /**
     * Returns the number of samples stored in the {@code BufferView}
     *
     * @return number of samples stored in the {@code BufferView}
     */
    public int size();

    /**
     * Returns the sample at the specified index
     *
     * @param i sample index
     * @return samplet at the specified index
     */
    public Object[] get(int i);

    /**
     * Releases the view, allowing a new view to be requested
     */
    public void release();

    /**
     * Executes the consumer function over all samples in the {@code
     * BufferView}
     *
     * @param c function to be performed
     */
    public void forEach(BiConsumer<Object[], BufferView> c);

    /**
     * Executes the consumer function over all samples in the {@code
     * BufferView} that satisfy the condition passed as parameter
     *
     * @param c sample condition
     * @param cond function to be performed
     */
    public void forEach(BiConsumer<Object[], BufferView> c, Expression cond);

    /**
     * Returns the number of samples whose timestamp is no older than the
     * specified duration with respect to the newest sample in the {@code
     * BufferView}
     *
     * @param d duration parameter
     * @return number of samples that satisfy the timestamp condition
     */
    public int samplesIn(Duration d);

    /**
     * Returns a new {@code BufferView} containing only the specified number
     * of samples.
     *
     * @param samples number of samples
     * @return new view with only the specified number of samples
     */
    public BufferView subView(int samples);

    /**
     * Returns a new {@code BufferView} containing only samples whose
     * timestamp is no older than the specified duration with respect to the
     * newest sample in the {@code BufferView}
     *
     * @param d duration parameter
     * @return new view with only the sample that satisfy the timestamp
     * condition
     */
    public BufferView subView(Duration d);

}
