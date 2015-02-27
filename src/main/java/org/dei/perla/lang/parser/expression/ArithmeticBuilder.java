package org.dei.perla.lang.parser.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.expression.Arithmetic;
import org.dei.perla.lang.expression.Arithmetic.ArithmeticOperation;
import org.dei.perla.lang.expression.CastFloat;
import org.dei.perla.lang.expression.Expression;

import java.util.List;

/**
 * @author Guido Rota 27/02/15.
 */
public final class ArithmeticBuilder implements BuilderNode {

    private final ArithmeticOperation op;
    private final BuilderNode b1;
    private final BuilderNode b2;

    public ArithmeticBuilder(ArithmeticOperation op, BuilderNode b1,
            BuilderNode b2) {
        this.op = op;
        this.b1 = b1;
        this.b2 = b2;
    }

    @Override
    public Expression build(List<Attribute> atts) {
        Expression e1 = b1.build(atts);
        Expression e2 = b2.build(atts);

        DataType d1 = e1.getType();
        DataType d2 = e2.getType();
        if (d1 != DataType.INTEGER || d1 != DataType.FLOAT ||
                d2 != DataType.INTEGER || d2 != DataType.FLOAT) {
            return BuilderNode.NULL_EXPRESSION;
        }

        // Modulo allowed only on integers
        if (op == ArithmeticOperation.MODULO &&
                (d1 != DataType.INTEGER || d2 != DataType.INTEGER)) {
            return BuilderNode.NULL_EXPRESSION;
        }

        DataType type = null;
        if (d1 == DataType.INTEGER && d1 == d2) {
            type = DataType.INTEGER;
        } if (d1 == DataType.FLOAT && d1 == d2) {
            type = DataType.FLOAT;
        } if (d1 == DataType.INTEGER && d2 == DataType.FLOAT) {
            e1 = new CastFloat(e1);
            type = DataType.FLOAT;
        } if (d1 == DataType.FLOAT && d2 == DataType.INTEGER) {
            e2 = new CastFloat(e2);
            type = DataType.FLOAT;
        }
        return new Arithmetic(op, e1, e2, type);
    }

}
