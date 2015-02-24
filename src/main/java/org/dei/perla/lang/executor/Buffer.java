package org.dei.perla.lang.executor;

import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;

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
     * {@code unmodifiableView} returns an unmodifiable view of the {@code
     * Buffer}.
     *
     * It is important to note that only a single unmodifiable buffer view can
     * be present at any time; hence, the user is required to release any
     * previously-created view before invoking this method.
     *
     * @return unmodifiable view of the {@code Buffer}.
     */
    public BufferView unmodifiableView();

}
