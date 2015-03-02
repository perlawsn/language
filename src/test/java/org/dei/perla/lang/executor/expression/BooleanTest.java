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
        Object res = new Not(trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        res = new Not(falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testAnd() {
        Object res = new And(trueExpr, trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        res = new And(falseExpr, trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        res = new And(trueExpr, falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        res = new And(falseExpr, falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testOr() {
        Object res = new Or(trueExpr, trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        res = new Or(falseExpr, trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        res = new Or(trueExpr, falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        res = new Or(falseExpr, falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testXor() {
        Object res = new Xor(trueExpr, trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        res = new Xor(falseExpr, trueExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        res = new Xor(trueExpr, falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        res = new Xor(falseExpr, falseExpr).compute(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));
    }

}
