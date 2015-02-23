package org.dei.perla.lang.executor;

import java.time.Duration;

/**
 * @author Guido Rota 23/02/15.
 */
public interface BufferView {

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
