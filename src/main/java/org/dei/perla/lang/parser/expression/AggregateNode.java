package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.*;
import org.dei.perla.lang.executor.statement.WindowSize;
import org.dei.perla.lang.parser.AggregationOperator;

import java.util.List;

/**
 * @author Guido Rota 09/03/15.
 */
public final class AggregateNode implements Node {

    private final AggregationOperator op;
    private final Node exp;
    private final WindowSize ws;
    private final Node filter;

    private AggregateNode(AggregationOperator op, Node exp,
            WindowSize ws, Node filter) {
        this.op = op;
        this.exp = exp;
        this.ws = ws;
        this.filter = filter;
    }

    public static Node create(AggregationOperator op, Node exp,
            WindowSize ws, Node filter) {
        if (filter != null && filter.getType() != null &&
                filter.getType() != DataType.BOOLEAN) {
            return new ErrorNode("Wrong filter predicate type. Expected " +
                    "BOOLEAN, found " + filter.getType());
        }

        // Count works for all expression types
        if (op == AggregationOperator.COUNT) {
            return new AggregateNode(op, exp, ws, filter);
        }

        // Defer further type checks at build
        if (exp.getType() == null) {
            return new AggregateNode(op, null, ws, filter);
        }

        if (!validExpressionType(op, exp.getType())) {
            return new ErrorNode("Cannot perform aggregation " + op +
                    " on expression of type " + exp.getType());
        }
        return new AggregateNode(op, exp, ws, filter);
    }

    /**
     * Checks if the expression type can be used for computing the selected
     * aggregation operation
     *
     * @param op   aggregation operation to perform
     * @param type operand type
     * @return true if the aggregation can be performed on the operand, false
     * otherwise
     */
    private static boolean validExpressionType(AggregationOperator op,
            DataType type) {
        // TODO: Allow MAX and MIN to be performed on TIMESTAMP expressions too
        return type == DataType.INTEGER || type == DataType.FLOAT;
    }

    public AggregationOperator getAggregationOperator() {
        return op;
    }

    public Node getExpression() {
        return exp;
    }

    public WindowSize getWindowSize() {
        return ws;
    }

    public Node getFilter() {
        return filter;
    }

    @Override
    public DataType getType() {
        if (op == AggregationOperator.COUNT) {
            return DataType.INTEGER;
        } else {
            return exp.getType();
        }
    }

    @Override
    public Expression build(List<Attribute> atts) {
        Expression cExp = null;
        Expression cWhere = null;

        if (filter != null) {
            cWhere = filter.build(atts);
            if (cWhere.getType() == null) {
                return Null.INSTANCE;
            }
        }

        if (exp != null) {
            cExp = exp.build(atts);
            if (!validExpressionType(op, cExp.getType())) {
                return Null.INSTANCE;
            }
        }

        switch (op) {
            case COUNT:
                return new CountAggregate(ws, cWhere);
            case SUM:
                return new SumAggregate(cExp, ws, cWhere);
            case AVG:
                return new AvgAggregate(cExp, ws, cWhere);
            case MIN:
                return new MinAggregate(cExp, ws, cWhere);
            case MAX:
                return new MaxAggregate(cExp, ws, cWhere);
            default:
                throw new RuntimeException("Unexpected aggregate type " + op);
        }
    }

}
