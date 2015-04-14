package org.dei.perla.lang.parser;

/**
 * A simple enum employed to distinguish between different types of
 * expressions.
 *
 * @author Guido Rota 14/04/15.
 */
public enum ExpressionType {

    // Simple expression, no IF EXIST and aggregations allowed
    SIMPLE,

    // Aggregation nodes allowed
    AGGREGATE,

    // IF EXIST allowed, aggregations forbidden
    EXECUTE_IF

}
