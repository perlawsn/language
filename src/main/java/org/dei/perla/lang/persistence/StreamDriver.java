package org.dei.perla.lang.persistence;

/**
 * @author Guido Rota 03/07/15.
 */
public interface StreamDriver {

    public Stream create(StreamDefinition stream) throws StreamException;

    public Stream open(String id) throws StreamException;

    public void delete(String id) throws StreamException;

}
