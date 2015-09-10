package org.dei.perla.lang.parser;

import org.dei.perla.core.fpc.DataType;

/**
 * A variale for storing the type of an {@link Fpc} {@link Attribute} while
 * parsing a PerLa query. This class is mainly employed to infer the type of
 * the {@link Attribute}s used in a query.
 *
 * @author Guido Rota 31/07/15.
 */
public final class TypeVariable {

    // The type is not final, as its value needs to be refined during the
    // parsing process
    private DataType type;

    public TypeVariable(DataType type) {
        this.type = type;
    }

    /**
     * Returns the current type stored in the variable
     *
     * @return Current type of the {@code TypeVariable}
     */
    public DataType getType() {
        return type;
    }

    /**
     * Changes the type inside the {@code TypeVariable} to the strictest type
     * between the current {@code TypeVariable}'s type and the {@link
     * DataType} passed as parameter. See {@code DataType.strictest()} for
     * further information
     *
     * @param other type boundary used during the restriction operation
     * @return true if the operation completed successfully, false if the
     * current {@code TypeVariable} type is not compatible with the boundary
     * passed as parameter
     */
    public boolean restrict(DataType other) {
        DataType t = DataType.strictest(type, other);
        if (t == null) {
            return false;
        }

        type = t;
        return true;
    }

    /**
     * Merges the inner type of two {@code TypeVariable}s. This operation is
     * equivalent to invoking the {@code restrict()} method on each {@code
     * TypeVariable} using the other's type as a parameter.
     *
     * @param t1 first {@code TypeVariable}
     * @param t2 second {@code TypeVariable}
     * @return true if the operation completed successfully, false if the
     * {@code TypeVariable}'s types are incompatible
     */
    public static boolean merge(TypeVariable t1, TypeVariable t2) {
        DataType t = DataType.strictest(t1.type, t2.type);
        if (t == null) {
            return false;
        }

        t1.type = t;
        t2.type = t;
        return true;
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
