package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.BindingException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 02/03/15.
 */
public class BitwiseTest {

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[] {
                intAtt,
                floatAtt
        });
    }

    @Test
    public void bitwiseANDTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(12, DataType.INTEGER);
        Expression e2 = Constant.create(5639, DataType.INTEGER);

        Expression e = Bitwise.createAND(e1, e2, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(12 & 5639));
    }

    @Test
    public void bitwiseANDIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Bitwise.createAND(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseANDNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Bitwise.createAND(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createAND(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseANDBindTest() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createAND(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createAND(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createAND(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseORTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(51452, DataType.INTEGER);
        Expression e2 = Constant.create(93849, DataType.INTEGER);

        Expression e = Bitwise.createOR(e1, e2, err);
        assertTrue(e.isComplete());
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(51452 | 93849));
    }

    @Test
    public void bitwiseORIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Bitwise.createOR(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseORNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Bitwise.createOR(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createOR(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseORBindTest() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createOR(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createOR(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createOR(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseXORTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(902833, DataType.INTEGER);
        Expression e2 = Constant.create(32112, DataType.INTEGER);

        Expression e = Bitwise.createXOR(e1, e2, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(902833 ^ 32112));
    }

    @Test
    public void bitwiseXORIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Bitwise.createXOR(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseXORNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Bitwise.createXOR(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createXOR(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseXORBindTest() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createXOR(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createXOR(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createXOR(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseNOTTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(7382, DataType.INTEGER);

        Expression e = Bitwise.createNOT(e1, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(~7382));
    }

    @Test
    public void bitwiseNOTIncompleteTest() {
        Errors err = new Errors();
        Expression e = Bitwise.createNOT(new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseNOTNullTest() {
        Errors err = new Errors();
        Expression e = Bitwise.createNOT(Constant.NULL, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseNOTBindTest() throws BindingException {
        Errors err = new Errors();
        Expression f = new Field("integer");

        Expression e = Bitwise.createNOT(f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseLSHTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(7382, DataType.INTEGER);
        Expression e2 = Constant.create(8, DataType.INTEGER);

        Expression e = Bitwise.createLSH(e1, e2, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 << 8));
    }

    @Test
    public void bitwiseLSHIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Bitwise.createLSH(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseLSHNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Bitwise.createLSH(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createLSH(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseLSHBindTest() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createLSH(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createLSH(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createLSH(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void bitwiseRSHTest() {
        Errors err = new Errors();
        Expression e1 = Constant.create(7382, DataType.INTEGER);
        Expression e2 = Constant.create(8, DataType.INTEGER);

        Expression e = Bitwise.createRSH(e1, e2, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        Object res = e.run(null, null);
        assertThat(res, equalTo(7382 >> 8));
    }

    @Test
    public void bitwiseRSHIncompleteTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);

        Expression e = Bitwise.createRSH(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseRSHNullTest() {
        Errors err = new Errors();
        Expression c = Constant.create(43, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Bitwise.createRSH(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Bitwise.createRSH(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void bitwiseRSHBindTest() throws BindingException {
        Errors err = new Errors();
        Field f = new Field("integer");
        Expression c = Constant.create(5, DataType.INTEGER);

        Expression e = Bitwise.createRSH(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createRSH(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Bitwise.createRSH(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

}
