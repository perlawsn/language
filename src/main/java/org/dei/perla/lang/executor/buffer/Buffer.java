package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.util.List;

/**
 * Data structure employed to store samples prior to evaluation of the
 * SELECTION clause
 *
 * @author Guido Rota 22/10/15.
 */
public interface Buffer {

    /**
     * Returns the structure of the samples that this {@code Buffer} can store
     *
     * @return sample structure
     */
    public List<Attribute> getAttributes();

    /**
     * Returns the number of samples stored in the {@code Buffer}
     *
     * @return number of samples stored in the {@code Buffer}
     */
    public int size();

    /**
     * Adds a new sample to the {@code Buffer}. It's the programmer's
     * responsibility to make sure that the structure of the sample is
     * consistent with the {@link Attribute} list of the {@code Buffer}
     *
     * @param sample sample to add
     */
    public void add(Object[] sample);

    /**
     * Returns an unmodifiable view of the {@code Buffer}. Before requesting
     * a new view, the old one must be released throught the {@code
     * BufferView.release()} method
     *
     * @return unmodifiable view of the buffer contents
     * @throws UnreleasedViewException when requesting a new view without
     * releasing the previous one
     */
    public BufferView createView() throws UnreleasedViewException;

}
