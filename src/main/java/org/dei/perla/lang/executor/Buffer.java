package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.time.Duration;
import java.util.List;

/**
 * @author Guido Rota 18/02/15.
 */
public interface Buffer {

    public List<Attribute> attributes();

    /**
     * {@code length} returns the number of records inside the backing {@link
     * Buffer}.
     *
     * @return number of records in the backing {@code Buffer}.
     */
    public int length();

    /**
     * {@code add} adds a {@link Record} to the {@code Buffer}.
     *
     * @param r {@link Record} to add
     */
    public void add(Record r);

    /**
     * {@code view} returns an immutable view on the buffer's contents.
     *
     * @return immutable view on the buffer's contents.
     */
    public BufferView view();

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
