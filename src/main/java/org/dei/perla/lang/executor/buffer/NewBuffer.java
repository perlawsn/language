package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;

import java.util.List;

/**
 * @author Guido Rota 22/10/15.
 */
public interface NewBuffer {

    public List<Attribute> getAttributes();

    public void add(Object[] sample);

    public NewBufferView getView();

}
