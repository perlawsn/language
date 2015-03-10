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
        Object res = new Not(trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        res = new Not(falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testAnd() {
        Object res = new And(trueExpr, trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        res = new And(falseExpr, trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        res = new And(trueExpr, falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        res = new And(falseExpr, falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testOr() {
        Object res = new Or(trueExpr, trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        res = new Or(falseExpr, trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        res = new Or(trueExpr, falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        res = new Or(falseExpr, falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testXor() {
        Object res = new Xor(trueExpr, trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));

        res = new Xor(falseExpr, trueExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        res = new Xor(trueExpr, falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(true));

        res = new Xor(falseExpr, falseExpr).run(null, null);
        assertTrue(res instanceof Bool);
        assertThat(res, equalTo(false));
    }

}
