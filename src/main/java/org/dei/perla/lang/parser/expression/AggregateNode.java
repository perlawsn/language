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

    private final AggregationOperator agg;
    private final Node op;
    private final WindowSize ws;
    private final Node filter;

    private AggregateNode(AggregationOperator agg, Node op,
            WindowSize ws, Node filter) {
        this.agg = agg;
        this.op = op;
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
        switch (op) {
            case COUNT:
                return true;
            case MIN:
            case MAX:
                return type == DataType.INTEGER || type == DataType.FLOAT ||
                        type == DataType.TIMESTAMP;
            default:
                return type == DataType.INTEGER || type == DataType.FLOAT;
        }
    }

    public AggregationOperator getAggregation() {
        return agg;
    }

    public Node getOperand() {
        return op;
    }

    public WindowSize getWindowSize() {
        return ws;
    }

    public Node getFilter() {
        return filter;
    }

    @Override
    public DataType getType() {
        switch (agg) {
            case COUNT:
                return DataType.INTEGER;
            case AVG:
                return DataType.FLOAT;
            default:
                return op.getType();
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

        if (op != null) {
            cExp = op.build(atts);
            if (!validExpressionType(agg, cExp.getType())) {
                return Null.INSTANCE;
            }
        }

        switch (agg) {
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
