package org.dei.perla.lang.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.lang.executor.BufferView;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author Guido Rota 23/02/15.
 */
public final class Sum implements Expression {

    private final Expression e1;
    private final Expression e2;

    // cached result type
    private final DataType tRes;

    public Sum(Expression e1, Expression e2) {
        DataType t1 = e1.getType();
        DataType t2 = e2.getType();
        if (t1 != DataType.INTEGER && t1 != DataType.FLOAT ||
                t2 != DataType.INTEGER && t2 != DataType.FLOAT) {
            throw new IllegalArgumentException("sum operands may only be of " +
                    "type INTEGER or FLOAT");
        }

        if (t1 == DataType.FLOAT || t2 == DataType.FLOAT) {
            tRes = DataType.FLOAT;
        } else {
            tRes = DataType.INTEGER;
        }

        this.e1 = e1;
        this.e2 = e2;
    }

    public Expression getOperand1() {
        return e1;
    }

    public Expression getOperand2() {
        return e2;
    }

    @Override
    public DataType getType() {
        return tRes;
    }

    @Override
    public Set<Attribute> attributes() {
        Set<Attribute> s = new TreeSet<>();
        s.addAll(e1.attributes());
        s.addAll(e2.attributes());
        return s;
    }

    @Override
    public Object compute(int i, BufferView buf) {
        if (tRes == DataType.INTEGER) {
            Integer op1 = (Integer) e1.compute(i, buf);
            Integer op2 = (Integer) e2.compute(i, buf);
            return new Constant(DataType.INTEGER, op1 + op2);

        } else {
            Float op1 = (Float) e1.compute(i, buf);
            Float op2 = (Float) e2.compute(i, buf);
            return new Constant(DataType.FLOAT, op1 + op2);
        }
    }

}
