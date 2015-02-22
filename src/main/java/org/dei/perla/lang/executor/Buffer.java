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

    public void add(Record r);

    /**
     * {@code range} returns an unmodifiable view on part of the {@code Buffer}.
     *
     * @param samples number of {@link Record}s to be included in the view.
     * @return unmodifiable view of the {@code Buffer}
     */
    public Buffer range(int samples);

    /**
     * {@code range} returns an unmodifiable view on part of the {@code Buffer}.
     *
     * @param d the {@link Duration} parameter identifies the number of
     *          records included in the {@code Buffer} view in terms of time
     *          offset from the newest sample.
     * @return unmodifiable view of the {@code Buffer}
     */
    public Buffer range(Duration d);

}
