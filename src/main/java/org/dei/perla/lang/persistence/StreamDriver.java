package org.dei.perla.lang.persistence;

import java.util.List;

/**
 * {@link Stream} management interface.
 *
 * @author Guido Rota 03/07/15.
 */
public interface StreamDriver {

    /**
     * Creates a new {@link Stream}.
     *
     * @param stream Characteristics of the stream to create
     * @return new {@link Stream} object
     * @throws StreamException in case an error occurs while creating the
     * {@link Stream}, or if the {@link Stream} identifier has already been
     * taken
     */
    public Stream create(StreamDefinition stream) throws StreamException;

    /**
     * Returns a reference to an existing {@link Stream}.
     *
     * @param id {@link Stream} identifier
     * @return {@link Stream} reference
     * @throws StreamException in case an error occurs while opening the
     * {@link Stream}
     */
    public Stream open(String id) throws StreamException;

    /**
     * <p> Returns a reference to an existing {@link Stream}. This method allows
     * the caller to specify a subset of the fields that are to be accessed.
     *
     * <p> When writing only a subset of the available fields, the remaining
     * fields will be populated with {@code null} or with their default value
     * (when specified).
     *
     * @param id {@link Stream} identifier
     * @param fields list of fields to be written
     * @return {@link Stream} reference
     * @throws StreamException in case an error occurs while opening the
     * {@link Stream}
     */
    public Stream open(String id, List<FieldDefinition> fields)
            throws StreamException;

}
