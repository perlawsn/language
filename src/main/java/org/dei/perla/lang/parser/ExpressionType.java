package org.dei.perla.lang.parser;

/**
 * A simple enum employed to distinguish between different types of
 * expressions.
 *
 * @author Guido Rota 14/04/15.
 */
public enum ExpressionType {

    // Constant expression, no aggregation, IF EXISTS and references to FPC
    // attributes
    CONSTANT,

    // Simple expression, no IF EXIST and aggregations allowed
    SIMPLE,

    // Aggregation nodes allowed
    AGGREGATE

}
