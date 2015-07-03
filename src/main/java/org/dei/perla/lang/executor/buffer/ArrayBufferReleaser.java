package org.dei.perla.lang.executor.buffer;

/**
 * <p> Releases a child view
 *
 * <p> The implementation as an abstract class was chosen instead of an
 * implementation as an interface to set the method {@code
 * releaseChildView} visibility to {@code protected}.
 *
 * @author Guido Rota 26/02/15.
 */
public abstract class ArrayBufferReleaser {

    protected abstract void releaseChildView(BufferView v);

}
