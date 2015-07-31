package org.dei.perla.lang.parser;

import org.dei.perla.core.registry.TypeClass;

/**
 * @author Guido Rota 31/07/15.
 */
public final class TypeVariable {

    private TypeClass type;

    public TypeVariable(TypeClass type) {
        this.type = type;
    }

    public TypeClass getTypeClass() {
        return type;
    }

    public boolean restrict(TypeClass other) {
        if (type == other) {
            return true;
        }

        TypeClass t1 = type;
        TypeClass t2 = other;
        if (type.ordinal() > other.ordinal()) {
            t1 = other;
            t2 = type;
        }

        if (t1 == TypeClass.ANY) {
            type = t2;
            return true;
        } else if (t1 == TypeClass.NUMERIC &&
                (t2 == TypeClass.INTEGER || t2 == TypeClass.FLOAT)) {
            type = t2;
            return true;
        } else {
            return false;
        }
    }

}
