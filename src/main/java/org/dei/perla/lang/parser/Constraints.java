package org.dei.perla.lang.parser;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class employed for building the EXECUTE IF constraints while
 * parsing PerLa expressions.
 *
 * @author Guido Rota 15/04/15.
 */
public class Constraints {

    private final List<String> identifiers = new ArrayList<>();
    private final List<Attribute> required = new ArrayList<>();

    public void addIdentifier(String id) {
        identifiers.add(id);
    }

    public void addRequired(String name, DataType type) {
        Attribute a = Attribute.create(name, type);
        required.add(a);
    }

}
