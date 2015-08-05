package org.dei.perla.lang.query.expression;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Guido Rota 02/03/15.
 */
public class BooleanTest {

    private static final Constant trueExp = Constant.TRUE;
    private static final Constant falseExp = Constant.FALSE;
    private static final Constant unknownExp = Constant.UNKNOWN;
    private static final Constant nullExp = Constant.NULL;

    @Test
    public void testNOT() {
        Expression e = new Not(trueExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Not(falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Not(unknownExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNOTUnknown() {
        Expression e = new Not(Constant.NULL);
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testAND() {
        Expression e = new Bool(BoolOperation.AND, trueExp, trueExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.AND, falseExp, trueExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Bool(BoolOperation.AND, trueExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Bool(BoolOperation.AND, falseExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testANDUnknown() {
        Expression e = new Bool(BoolOperation.AND, trueExp, nullExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.AND, falseExp, nullExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Bool(BoolOperation.AND, nullExp, trueExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.AND, nullExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Bool(BoolOperation.AND, nullExp, nullExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testOR() {
        Expression e = new Bool(BoolOperation.OR, trueExp, trueExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.OR, falseExp, trueExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.OR, trueExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.OR, falseExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testORUnknown() {
        Expression e = new Bool(BoolOperation.OR, trueExp, nullExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.OR, falseExp, nullExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.OR, nullExp, trueExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.OR, nullExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.OR, nullExp, nullExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testXOR() {
        Expression e = new Bool(BoolOperation.XOR, trueExp, trueExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        e = new Bool(BoolOperation.XOR, falseExp, trueExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.XOR, trueExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        e = new Bool(BoolOperation.XOR, falseExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testXORUnknown() {
        Expression e = new Bool(BoolOperation.XOR, trueExp, nullExp);
        LogicValue res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.XOR, falseExp, nullExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.XOR, nullExp, trueExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.XOR, nullExp, falseExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = new Bool(BoolOperation.XOR, nullExp, nullExp);
        res = (LogicValue) e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

}
