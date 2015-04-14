package org.dei.perla.lang.executor;

import org.dei.perla.core.sample.Sample;

/**
 * @author Guido Rota 18/02/15.
 */
public interface Buffer {

    /**
     * {@code length} returns the number of samples inside the backing {@link
     * Buffer}.
     *
     * @return number of samples in the backing {@code Buffer}.
     */
    public int length();

    /**
     * {@code add} adds a {@link Sample} to the {@code Buffer}.
     *
     * @param r {@link Sample} to add
     */
    public void add(Sample r);

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
