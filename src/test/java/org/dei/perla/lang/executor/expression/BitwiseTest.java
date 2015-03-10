package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 02/03/15.
 */
public class BitwiseTest {

    @Test
    public void bitwiseAndTest() {
        Expression e1 = new Constant(12, DataType.INTEGER);
        Expression e2 = new Constant(5639, DataType.INTEGER);

        Expression and = Bitwise.createAND(e1, e2);
        assertTrue(and.isComplete());
        assertThat(and.getType(), equalTo(DataType.INTEGER));
        Object res = and.run(null, null);
        assertThat(res, equalTo(12 & 5639));
    }

    @Test
    public void bitwiseOrTest() {
        Expression e1 = new Constant(51452, DataType.INTEGER);
        Expression e2 = new Constant(93849, DataType.INTEGER);

        Expression and = Bitwise.createOR(e1, e2);
        assertTrue(and.isComplete());
        assertThat(and.getType(), equalTo(DataType.INTEGER));
        Object res = and.run(null, null);
        assertThat(res, equalTo(51452 | 93849));
    }

    @Test
    public void bitwiseXorTest() {
        Expression e1 = new Constant(902833, DataType.INTEGER);
        Expression e2 = new Constant(32112, DataType.INTEGER);

        Expression and = Bitwise.createXOR(e1, e2);
        assertTrue(and.isComplete());
        assertThat(and.getType(), equalTo(DataType.INTEGER));
        Object res = and.run(null, null);
        assertThat(res, equalTo(32112 ^ 902833));
    }

    @Test
    public void bitwiseNotTest() {
        Expression e1 = new Constant(7382, DataType.INTEGER);

        Expression and = Bitwise.createNOT(e1);
        assertTrue(and.isComplete());
        assertThat(and.getType(), equalTo(DataType.INTEGER));
        Object res = and.run(null, null);
        assertThat(res, equalTo(~7382));
    }

    @Test
    public void bitwiseShiftLTest() {
        Expression e1 = new Constant(7382, DataType.INTEGER);
        Expression e2 = new Constant(8, DataType.INTEGER);

        Expression and = Bitwise.createLSH(e1, e2);
        assertTrue(and.isComplete());
        assertThat(and.getType(), equalTo(DataType.INTEGER));
        Object res = and.run(null, null);
        assertThat(res, equalTo(7382 << 8));
    }

    @Test
    public void bitwiseShiftRTest() {
        Expression e1 = new Constant(7382, DataType.INTEGER);
        Expression e2 = new Constant(8, DataType.INTEGER);

        Expression and = Bitwise.createRSH(e1, e2);
        assertTrue(and.isComplete());
        assertThat(and.getType(), equalTo(DataType.INTEGER));
        Object res = and.run(null, null);
        assertThat(res, equalTo(7382 >> 8));
    }

}
