package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 02/03/15.
 */
public class BooleanTest {

    private static final Expression trueExpr =
            new Constant(true, DataType.BOOLEAN);
    private static final Expression falseExpr =
            new Constant(false, DataType.BOOLEAN);

    private static List<Attribute> atts;

    @BeforeClass
    public static void setup() {
        atts = new ArrayList<>();
        atts.add(Attribute.create("integer", DataType.INTEGER));
        atts.add(Attribute.create("boolean", DataType.BOOLEAN));
    }

    @Test
    public void testNOT() {
        Expression e = Bool.createNOT(trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        e = Bool.createNOT(falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testNOTUnknown() {
        Expression e = Bool.createNOT(Constant.NULL_BOOLEAN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testNOTError() {
        Expression err = new ErrorExpression("test");

        Expression e = Bool.createNOT(err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testNOTRebuild() {
        Expression f = new Field("boolean");

        Expression e = Bool.createNOT(f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testAND() {
        Expression e = Bool.createAND(trueExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        e = Bool.createAND(falseExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        e = Bool.createAND(trueExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        e = Bool.createAND(falseExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testANDUnknown() {
        Expression nul = Constant.NULL_BOOLEAN;

        Expression e = Bool.createAND(trueExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createAND(falseExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createAND(nul, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createAND(nul, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createAND(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testANDError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

        Expression e = Bool.createAND(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createAND(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createAND(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testANDRebuild() {
        Constant c = new Constant(true, DataType.BOOLEAN);
        Field f = new Field("boolean");

        Expression e = Bool.createAND(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createAND(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createAND(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testOR() {
        Expression e = Bool.createOR(trueExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        e = Bool.createOR(falseExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        e = Bool.createOR(trueExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        e = Bool.createOR(falseExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testORUnknown() {
        Expression nul = Constant.NULL_BOOLEAN;

        Expression e = Bool.createOR(trueExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createOR(falseExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createOR(nul, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createOR(nul, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createOR(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testORError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

        Expression e = Bool.createOR(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createOR(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createOR(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testORRebuild() {
        Constant c = new Constant(true, DataType.BOOLEAN);
        Field f = new Field("boolean");

        Expression e = Bool.createOR(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createOR(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createOR(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testXOR() {
        Expression e = Bool.createXOR(trueExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));

        e = Bool.createXOR(falseExpr, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        e = Bool.createXOR(trueExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(true));

        e = Bool.createXOR(falseExpr, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertTrue(res instanceof Boolean);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testXORUnknown() {
        Expression nul = Constant.NULL_BOOLEAN;

        Expression e = Bool.createXOR(trueExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createXOR(falseExpr, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createXOR(nul, trueExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createXOR(nul, falseExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bool.createXOR(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testXORError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

        Expression e = Bool.createXOR(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createXOR(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bool.createXOR(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testXORRebuild() {
        Constant c = new Constant(true, DataType.BOOLEAN);
        Field f = new Field("boolean");

        Expression e = Bool.createXOR(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createXOR(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bool.createXOR(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

}
