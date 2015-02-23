package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;

import java.time.Duration;
import java.util.List;

/**
 * {@code BufferView} is a static view on a {@link Buffer} portion.
 *
 * @author Guido Rota 23/02/15.
 */
public interface BufferView {

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

    /**
     * {@code view} returns a sub-view on the {@link Buffer}. Differently
     * from {@link Buffer.unmodifiableView}, this method can be invoked
     * several times to create as many sub-views as desired. However, the
     * user is still required to release each of the child {@code BufferView}
     * objects.
     *
     * @param samples number of {@link Record}s to be included in the view.
     * @return unmodifiable view of the buffer. The resulting view may
     *         contain less records than requested if the source buffer does
     *         not contain enough data.
     */
    public BufferView view(int samples);

    /**
     * {@code view} returns a sub-view on the {@link Buffer}. Differently
     * from {@link Buffer.unmodifiableView}, this method can be invoked
     * several times to create as many sub-views as desired. However, the
     * user is still required to release each of the child {@code BufferView}
     * objects.
     *
     * @param d the {@link Duration} parameter identifies the number of
     *          records included in the {@code Buffer} view in terms of time
     *          offset from the newest sample.
     * @return view of the buffer. The resulting view may contain less
     *         records than requested if the source buffer does not contain
     *         enough data.
     */
    public BufferView view(Duration d);

}
