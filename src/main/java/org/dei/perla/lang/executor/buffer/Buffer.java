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

    public List<Attribute> getAttributes();

    public int size();

    public void add(Object[] sample);

    public BufferView createView() throws UnreleasedViewException;

}
