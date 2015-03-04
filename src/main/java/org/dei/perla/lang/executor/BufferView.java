package org.dei.perla.lang.executor;

import org.dei.perla.lang.executor.expression.Expression;

import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * {@code BufferView} is a static view on a portion of the content of a {@link
 * Buffer}.
 *
 * @author Guido Rota 23/02/15.
 */
public interface BufferView {

    /**
     * {@code length} returns the number of records in the {@code BufferView}.
     *
     * @return number of records in the {@code BufferView}.
     */
    public int length();

    /**
     * {@code release} releases the records in the {@code BufferView}. After
     * calling this method, further accesses to the contents of this object
     * will result in undefined behaviour.
     */
    public void release();

    /**
     * {@code get} retrieves the ith oldest record in the {@code BufferView}.
     *
     * @param i index of the record to retrieve
     * @return Object array containing all the fields in the selected record
     * @throws IndexOutOfBoundsException if the index if out of range
     */
    public Object[] get(int i);

    public void forEach(BiConsumer<Object[], BufferView> c);

    public void forEach(BiConsumer<Object[], BufferView> c, Expression cond);

    public int recordsIn(Duration d);

    public BufferView subView(int samples);

    public BufferView subView(Duration d);

    public List<BufferView> groupBy(Duration d, int count);

    public List<BufferView> groupBy(List<? extends Expression> fields);

}
