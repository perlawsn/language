package org.dei.perla.lang.persistence.memory;

import org.dei.perla.lang.persistence.StreamDefinition;
import org.dei.perla.lang.persistence.StreamDriver;
import org.dei.perla.lang.persistence.StreamException;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in-memory {@link StreamDriver} implementation.
 *
 * @author Guido Rota 07/07/15.
 */
public class MemoryStreamDriver implements StreamDriver {

    private final Map<String, MemoryStream> streams = new HashMap<>();

    @Override
    public synchronized MemoryStream create(StreamDefinition def)
            throws StreamException {
        String id = def.getId();
        if (streams.containsKey(id)) {
            throw new StreamException("Cannot create stream, id '" + id
                     + "' has already been taken");
        }

        MemoryStream s = new MemoryStream(def);
        streams.put(id, s);
        return s;
    }

    @Override
    public MemoryStream open(String id) throws StreamException {
        MemoryStream s = streams.get(id);
        if (s == null) {
            throw new StreamException("Stream '" + id + "' does not exist");
        }

        return s;
    }

}
