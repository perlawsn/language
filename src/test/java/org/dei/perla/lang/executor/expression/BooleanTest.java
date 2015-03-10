package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.lang.executor.BufferView;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 02/03/15.
 */
public class BooleanTest {

    private static BufferView view;

    private static Expression trueExpr = new Constant(true, DataType.BOOLEAN);
    private static Expression falseExpr = new Constant(false, DataType.BOOLEAN);

    @Test
    public void testNot() {
        Expression not = Bool.createNOT(trueExpr);
        assertTrue(not.isComplete());
        Object res = not.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        not = Bool.createNOT(falseExpr);
        assertTrue(not.isComplete());
        res = not.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testAnd() {
        Expression and = Bool.createAND(trueExpr, trueExpr);
        assertTrue(and.isComplete());
        Object res = and.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        and = Bool.createAND(falseExpr, trueExpr);
        assertTrue(and.isComplete());
        res = res = and.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        and = Bool.createAND(trueExpr, falseExpr);
        assertTrue(and.isComplete());
        res = res = and.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        and = Bool.createAND(falseExpr, falseExpr);
        assertTrue(and.isComplete());
        res = res = and.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testOr() {
        Expression or = Bool.createOR(trueExpr, trueExpr);
        assertTrue(or.isComplete());
        Object res = or.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        or = Bool.createOR(falseExpr, trueExpr);
        assertTrue(or.isComplete());
        res = or.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        or = Bool.createOR(trueExpr, falseExpr);
        assertTrue(or.isComplete());
        res = or.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        or = Bool.createOR(falseExpr, falseExpr);
        assertTrue(or.isComplete());
        res = or.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testXor() {
        Expression xor = Bool.createXOR(trueExpr, trueExpr);
        assertTrue(xor.isComplete());
        Object res = xor.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        xor = Bool.createXOR(falseExpr, trueExpr);
        assertTrue(xor.isComplete());
        res = xor.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        xor = Bool.createXOR(trueExpr, falseExpr);
        assertTrue(xor.isComplete());
        res = xor.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        xor = Bool.createXOR(falseExpr, falseExpr);
        assertTrue(xor.isComplete());
        res = xor.run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));
    }

}
