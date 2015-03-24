package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
        Expression e1 = Constant.create(12, DataType.INTEGER);
        Expression e2 = Constant.create(5639, DataType.INTEGER);

        Expression e = Bitwise.createAND(e1, e2);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(12 & 5639));
    }

    @Test
    public void bitwiseANDNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Bitwise.createAND(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseANDErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bitwise.createAND(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createAND(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createAND(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void bitwiseANDBindTest() {
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createAND(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        Set<String> fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createAND(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createAND(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void bitwiseORTest() {
        Expression e1 = Constant.create(51452, DataType.INTEGER);
        Expression e2 = Constant.create(93849, DataType.INTEGER);

        Expression e = Bitwise.createOR(e1, e2);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(51452 | 93849));
    }

    @Test
    public void bitwiseORNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Bitwise.createOR(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseORErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bitwise.createOR(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createOR(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createOR(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void bitwiseORBindTest() {
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createOR(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        Set<String> fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createOR(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createOR(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void bitwiseXORTest() {
        Expression e1 = Constant.create(902833, DataType.INTEGER);
        Expression e2 = Constant.create(32112, DataType.INTEGER);

        Expression e = Bitwise.createXOR(e1, e2);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(902833 ^ 32112));
    }

    @Test
    public void bitwiseXORNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Bitwise.createXOR(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseXORErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bitwise.createXOR(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createXOR(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createXOR(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void bitwiseXORBindTest() {
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createXOR(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        Set<String> fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createXOR(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createXOR(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void bitwiseNOTTest() {
        Expression e1 = Constant.create(7382, DataType.INTEGER);

        Expression e = Bitwise.createNOT(e1);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(~7382));
    }

    @Test
    public void bitwiseNOTNullTest() {
        Expression e = Bitwise.createNOT(Constant.NULL_INTEGER);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseNOTErrorTest() {
        Expression err = new ErrorExpression("test");

        Expression e = Bitwise.createNOT(err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void bitwiseNOTBindTest() {
        Expression f = new Field("integer");

        Expression e = Bitwise.createNOT(f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        Set<String> fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void bitwiseLSHTest() {
        Expression e1 = Constant.create(7382, DataType.INTEGER);
        Expression e2 = Constant.create(8, DataType.INTEGER);

        Expression e = Bitwise.createLSH(e1, e2);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 << 8));
    }

    @Test
    public void bitwiseLSHNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Bitwise.createLSH(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseLSHErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bitwise.createLSH(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createLSH(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createLSH(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void bitwiseLSHBindTest() {
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createLSH(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        Set<String> fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createLSH(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createLSH(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void bitwiseRSHTest() {
        Expression e1 = Constant.create(7382, DataType.INTEGER);
        Expression e2 = Constant.create(8, DataType.INTEGER);

        Expression e = Bitwise.createRSH(e1, e2);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 >> 8));
    }

    @Test
    public void bitwiseRSHNullTest() {
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL_INTEGER;

        Expression e = Bitwise.createRSH(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseRSHErrorTest() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Bitwise.createRSH(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createRSH(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Bitwise.createRSH(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void bitwiseRSHBindTest() {
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createRSH(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        Set<String> fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createRSH(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Bitwise.createRSH(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        fields = e.getFields();
        assertThat(fields.size(), equalTo(1));
        assertTrue(fields.contains("integer"));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

}
