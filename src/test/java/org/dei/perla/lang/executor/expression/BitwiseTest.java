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
public class BitwiseTest {

    private static List<Attribute> atts;

    @BeforeClass
    public static void setup() {
        atts = new ArrayList<>();
        atts.add(Attribute.create("integer", DataType.INTEGER));
        atts.add(Attribute.create("float", DataType.FLOAT));
    }

    @Test
    public void bitwiseANDTest() {
        Expression e1 = new Constant(12, DataType.INTEGER);
        Expression e2 = new Constant(5639, DataType.INTEGER);

        Expression e = Bitwise.createAND(e1, e2);
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(12 & 5639));
    }

    @Test
    public void bitwiseANDNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Bitwise.createAND(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseANDRebuildTest() {
        Field f = new Field("integer");
        Constant c = new Constant(5, DataType.INTEGER);

        Expression e = Bitwise.createAND(f, c);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createAND(c, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createAND(f, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseORTest() {
        Expression e1 = new Constant(51452, DataType.INTEGER);
        Expression e2 = new Constant(93849, DataType.INTEGER);

        Expression e = Bitwise.createOR(e1, e2);
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(51452 | 93849));
    }

    @Test
    public void bitwiseORNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Bitwise.createOR(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseORRebuildTest() {
        Field f = new Field("integer");
        Constant c = new Constant(5, DataType.INTEGER);

        Expression e = Bitwise.createOR(f, c);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createOR(c, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createOR(f, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseXORTest() {
        Expression e1 = new Constant(902833, DataType.INTEGER);
        Expression e2 = new Constant(32112, DataType.INTEGER);

        Expression e = Bitwise.createXOR(e1, e2);
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(902833 ^ 32112));
    }

    @Test
    public void bitwiseXORNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Bitwise.createXOR(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseXORRebuildTest() {
        Field f = new Field("integer");
        Constant c = new Constant(5, DataType.INTEGER);

        Expression e = Bitwise.createXOR(f, c);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createXOR(c, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createXOR(f, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseNOTTest() {
        Expression e1 = new Constant(7382, DataType.INTEGER);

        Expression e = Bitwise.createNOT(e1);
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(~7382));
    }

    @Test
    public void bitwiseNOTNullTest() {
        Expression e = Bitwise.createNOT(Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseNOTRebuildTest() {
        Expression f = new Field("integer");

        Expression e = Bitwise.createNOT(f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseLSHTest() {
        Expression e1 = new Constant(7382, DataType.INTEGER);
        Expression e2 = new Constant(8, DataType.INTEGER);

        Expression e = Bitwise.createLSH(e1, e2);
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 << 8));
    }

    @Test
    public void bitwiseLSHNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Bitwise.createLSH(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseLSHRebuildTest() {
        Field f = new Field("integer");
        Constant c = new Constant(5, DataType.INTEGER);

        Expression e = Bitwise.createLSH(f, c);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createLSH(c, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createLSH(f, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseRSHTest() {
        Expression e1 = new Constant(7382, DataType.INTEGER);
        Expression e2 = new Constant(8, DataType.INTEGER);

        Expression e = Bitwise.createRSH(e1, e2);
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 >> 8));
    }

    @Test
    public void bitwiseRSHNullTest() {
        Expression c = new Constant(43, DataType.INTEGER);

        Expression e = Bitwise.createRSH(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseRSHRebuildTest() {
        Field f = new Field("integer");
        Constant c = new Constant(5, DataType.INTEGER);

        Expression e = Bitwise.createRSH(f, c);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createRSH(c, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Bitwise.createRSH(f, f);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

}
