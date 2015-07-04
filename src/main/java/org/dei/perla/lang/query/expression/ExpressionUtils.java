package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;

import java.util.Collection;
import java.util.List;

/**
 * @author Guido Rota 04/07/15.
 */
public final class ExpressionUtils {

    /**
     * Helper method employed to determine the index in the bound {@link
     * Attribute} {@link List} of a given {@link Attribute} identifier.
     *
     * @param id {@link Attribute} identifier
     * @param bound bound {@link Attribute} {@link List}
     * @return index of the {@link Attribute} with the given identifier, -1
     * if not found
     */
    public static int indexOf(String id, List<Attribute> bound) {
        int i = 0;
        for (Attribute a : bound) {
            if (a.getId().equals(id)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Searches the {@link Attribute} with the specified id in the {@link
     * Collection} passed as parameter.
     *
     * @param id {@link Attribute} identifier
     * @param atts {@link Collection} of {@link Attribute}s
     * @return {@link Attribute} with the specified id, null if not found
     */
    public static Attribute getById(String id, Collection<Attribute> atts) {
        for (Attribute a : atts) {
            if (a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Evaluates the value of a constant {@link Expression}.
     *
     * @param exp {@link Expression} to evaluate
     * @param type desired output {@link DataType}
     * @param err error data structure
     * @return value of the constant {@link Expression}
     */
    public static Object evaluateConstant(Expression exp, DataType type,
            Errors err) {
        if (!(exp instanceof Constant)) {
            err.addError("Expression '" + exp + "' is not constant");
            return null;
        } else if (exp == Constant.NULL) {
            return null;
        }

        switch (type) {
            case INTEGER:
                exp = CastInteger.create(exp, err);
                break;
            case FLOAT:
                exp = CastFloat.create(exp, err);
                break;
            case STRING:
                return ((Constant) exp).getValue().toString();
            case BOOLEAN:
                if (exp.getType() != DataType.BOOLEAN) {
                    err.addError("Cannot convert expression '" + exp + "' of " +
                            "type '" + exp.getType() + "' to type 'boolean'");
                    return null;
                }
                break;
            case TIMESTAMP:
                if (exp.getType() != DataType.TIMESTAMP) {
                    err.addError("Cannot convert expression '" + exp + "' of " +
                            "type '" + exp.getType() + "' to type 'timestamp'");
                    return null;
                }
                break;
            case ID:
                if (exp.getType() != DataType.ID) {
                    err.addError("Cannot convert expression '" + exp + "' of " +
                            "type '" + exp.getType() + "' to type 'id'");
                    return null;
                }
                break;
        }

        return ((Constant) exp).getValue();
    }

}
