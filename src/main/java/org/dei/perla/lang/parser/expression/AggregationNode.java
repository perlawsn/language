package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.statement.WindowSize;
import org.dei.perla.lang.parser.AggregationOperator;

import java.util.List;

/**
 * @author Guido Rota 09/03/15.
 */
public class AggregationNode implements Node {

    private final AggregationOperator op;
    private final Node exp;
    private final WindowSize ws;
    private final Node where;

    private AggregationNode(AggregationOperator op, Node exp,
            WindowSize ws, Node where) {
        this.op = op;
        this.exp = exp;
        this.ws = ws;
        this.where = where;
    }

    public static Node create(AggregationOperator op, Node exp,
            WindowSize ws, Node where) {
        if (where != null && where.getType() != null &&
                where.getType() != DataType.BOOLEAN) {
            return new ErrorNode("Wrong filter predicate type. Expected " +
                    "BOOLEAN, found " + where.getType());
        }

        // Count doesn't really care about the expression type. Moreover, if
        // et is null then we cannot make any compile-time check (they are
        // deferred to the build phase).
        if (op == AggregationOperator.COUNT || exp.getType() == null) {
            return new AggregationNode(op, null, ws, where);
        }

        if (!validExpressionType(exp.getType())) {
            return new ErrorNode("Cannot perform aggregation " + op +
                    " on expression of type " + exp.getType());
        }
        return new AggregationNode(op, exp, ws, where);
    }

    private static boolean validExpressionType(DataType type) {
        return type == DataType.INTEGER || type == DataType.FLOAT;
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

        if (where != null) {
            cWhere = where.build(atts);
        }
        throw new RuntimeException("unimplemented");
    }

}
