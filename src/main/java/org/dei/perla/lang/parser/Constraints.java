package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.DataTemplate;
import org.dei.perla.core.registry.TypeClass;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class employed for building the EXECUTE IF constraints while
 * parsing PerLa expressions.
 *
 * @author Guido Rota 15/04/15.
 */
public class Constraints {

    private boolean all = false;
    private final List<String> identifiers = new ArrayList<>();
    private final List<DataTemplate> required = new ArrayList<>();

    public void setExistsAll() {
        all = false;
    }

    public void addIdentifier(String id) {
        identifiers.add(id);
    }

    public void addRequired(String id, TypeClass typeClass) {
        DataTemplate t = DataTemplate.create(id, typeClass);
        required.add(t);
    }

    public void addRequired(DataTemplate t) {
        required.add(t);
    }

}
