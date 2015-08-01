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
        TypeClass t = strictest(type, other);
        if (t == null) {
            return false;
        }

        type = t;
        return true;
    }

    public static boolean merge(TypeVariable t1, TypeVariable t2) {
        TypeClass t = strictest(t1.type, t2.type);
        if (t == null) {
            return false;
        }

        t1.type = t;
        t2.type = t;
        return true;
    }

    private static TypeClass strictest(TypeClass t1, TypeClass t2) {
        if (t1 == t2) {
            return t1;
        }

        if (t1.ordinal() > t2.ordinal()) {
            TypeClass tmp = t1;
            t1 = t2;
            t2 = tmp;
        }

        if (t1 == TypeClass.ANY) {
            return t2;
        } else if (t1 == TypeClass.NUMERIC &&
                (t2 == TypeClass.INTEGER || t2 == TypeClass.FLOAT)) {
            return t2;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
