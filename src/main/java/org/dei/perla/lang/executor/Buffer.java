package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

import java.time.Duration;
import java.util.List;

/**
 * @author Guido Rota 18/02/15.
 */
public interface Buffer {

    /**
     * {@code attributes} returns the list of {@link Attribute}s that compose
     * each record contained in this {@link BufferView}. The implementation
     * of the query executor guarantees that every record stored inside this
     * object shares the same {@link Attribute}s and {@link Attribute} order.
     *
     * @return list of {@link Attribute}s that compose each buffer record
     */
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
     * {@code unmodifiableView} returns an unmodifiable view on part of the
     * {@code Buffer}.
     *
     * It is important to note that only a single unmodifiable
     * {@link BufferView} can be present at any time; hence, the user is
     * required to release any previously-created view before invoking this
     * method.
     *
     * @param samples number of {@link Record}s to be included in the view.
     * @return unmodifiable view of the {@code Buffer}. The resulting view
     *         may contain less records than requested if the source buffer does
     *         not contain enough data.
     */
    public BufferView unmodifiableView(int samples);

    /**
     * {@code unmodifiableView} returns an unmodifiable view on part of the
     * {@code Buffer}.
     *
     * It is important to note that only a single unmodifiable
     * {@link BufferView} can be present at any time; hence, the user is
     * required to release any previously-created view before invoking this
     * method.
     *
     * @param d the {@link Duration} parameter identifies the number of
     *          records included in the {@code Buffer} view in terms of time
     *          offset from the newest sample.
     * @return unmodifiable view of the {@code Buffer}. The resulting view
     *         may contain less records than requested if the source buffer
     *         does not contain enough data.
     */
    public BufferView unmodifiableView(Duration d);

}
