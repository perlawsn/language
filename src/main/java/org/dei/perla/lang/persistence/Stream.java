package org.dei.perla.lang.persistence;

import java.util.List;

/**
 * @author Guido Rota 03/07/15.
 */
public interface Stream {

    public String getId();

    public List<FieldDefinition> getFields();

    public void add(Object[] record) throws StreamException ;

}
