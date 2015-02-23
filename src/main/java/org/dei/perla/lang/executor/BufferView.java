package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;

import java.time.Duration;
import java.util.List;

/**
 * @author Guido Rota 23/02/15.
 */
public interface BufferView {

    public List<Attribute> attributes();

    /**
     * {@code length} returns the number of records inside the backing {@link
     * Buffer}.
     *
     * @return number of records in the backing {@code Buffer}.
     */
    public int length();

    /**
     * {@code get} retrieves the ith oldest record from the buffer.
     *
     * @param i index of the record to retrieve
     * @return Object array containing all the fields in the selected record
     * @throws IndexOutOfBoundsException if the index if out of range
     */
    public Object[] get(int i);

    /**
     * {@code range} returns an unmodifiable view on part of the {@code Buffer}.
     *
     * @param samples number of {@link Record}s to be included in the view.
     * @return unmodifiable view of the {@code Buffer}
     */
    public BufferView range(int samples);

    /**
     * {@code range} returns an unmodifiable view on part of the {@code Buffer}.
     *
     * @param d the {@link Duration} parameter identifies the number of
     *          records included in the {@code Buffer} view in terms of time
     *          offset from the newest sample.
     * @return unmodifiable view of the {@code Buffer}
     */
    public BufferView range(Duration d);

}
