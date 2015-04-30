package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 23/02/15.
 */
public class ComparisonTest {

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);
    private static Attribute boolAtt =
            Attribute.create("boolean", DataType.BOOLEAN);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt,
                boolAtt
        });
    }

    private static final Expression trueExpr = Constant.TRUE;
    private static final Expression falseExpr = Constant.FALSE;

    private static final Expression t1 =
            Constant.create(Instant.parse("2015-02-23T15:07:45.000Z"), DataType.TIMESTAMP);
    private static final Expression t2 =
            Constant.create(Instant.parse("2015-02-23T15:08:45.000Z"), DataType.TIMESTAMP);

    @Test
    public void testEQ() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createEQ(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] sample = new Object[]{4};
        Object res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{3};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createEQ(new Field("float"), c, err);
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{4.4f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{3.3f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create("4", DataType.STRING);
        e = Comparison.createEQ(new Field("string"), c, err);
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{"4"};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{"3"};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createEQ(trueExpr, trueExpr, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createEQ(trueExpr, falseExpr, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createEQ(t1, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createEQ(t1, t2, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testEQIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createEQ(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createEQ(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testEQNull() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createEQ(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createEQ(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createEQ(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testEQBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createEQ(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createEQ(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createEQ(f, f, err);
        assertFalse(e.isComplete());
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testNE() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createNE(new Field("integer"), c, err);
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertTrue(e.isComplete());
        Object[] sample = new Object[]{4};
        Object res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{3};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createNE(new Field("float"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{4.4f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{3.3f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create("4", DataType.STRING);
        e = Comparison.createNE(new Field("string"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{"4"};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{"3"};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createNE(trueExpr, trueExpr, err).run(null, null);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createNE(trueExpr, falseExpr, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createNE(t1, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createNE(t1, t2, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testNEIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createNE(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createNE(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNENull() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createNE(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createNE(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createNE(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNEBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createNE(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createNE(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createNE(f, f, err);
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
    public void testGT() {
        Errors err = new Errors();

        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createGT(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] sample = new Object[]{4};
        Object res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{3};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGT(new Field("integer"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createGT(c, new Field("float"), err);
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{4.4f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{3.3f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGT(new Field("float"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createGT(t1, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createGT(t1, t2, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createGT(t2, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testGTIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createGT(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGT(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGTNull() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createGT(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGT(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGT(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGTBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createGT(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createGT(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createGT(f, f, err);
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
    public void testGE() {
        Errors err = new Errors();

        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createGE(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] sample = new Object[]{4};
        Object res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{3};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGE(new Field("integer"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createGE(c, new Field("float"), err);
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{4.4f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{3.3f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGE(new Field("float"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createGE(t1, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createGE(t1, t2, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createGE(t2, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testGEIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createGE(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGE(new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGENull() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createGE(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGE(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGE(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGEBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createGE(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createGE(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createGE(f, f, err);
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
    public void testLT() {
        Errors err = new Errors();

        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createLT(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] sample = new Object[]{4};
        Object res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{3};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLT(new Field("integer"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createLT(c, new Field("float"), err);
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{4.4f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        sample = new Object[]{3.3f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLT(new Field("float"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createLT(t1, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createLT(t1, t2, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createLT(t2, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLTIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createLT(c, new Field("integer"), err);
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLT(new Field("integer"), c, err);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLTNull() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createLT(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLT(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLT(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLTBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createLT(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createLT(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createLT(f, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testLE() {
        Errors err = new Errors();

        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createLE(c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] sample = new Object[]{4};
        Object res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{3};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLE(new Field("integer"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createLE(c, new Field("float"), err);
        assertTrue(err.isEmpty());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        sample = new Object[]{4.4f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        sample = new Object[]{3.3f};
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLE(new Field("float"), c, err);
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(sample, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createLE(t1, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createLE(t1, t2, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createLE(t2, t1, err).run(null, null);
        assertTrue(err.isEmpty());
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLEIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createLE(c, new Field("integer"), err);
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLE(new Field("integer"), c, err);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLENull() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createLE(c, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLE(nul, c, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLE(nul, nul, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLEBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createLE(f, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createLE(c, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Comparison.createLE(f, f, err);
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
    public void testBetween() {
        Errors err = new Errors();
        Expression min = Constant.create(-0.5f, DataType.FLOAT);
        Expression max = Constant.create(456f, DataType.FLOAT);
        Expression middle = Constant.create(23f, DataType.FLOAT);

        Expression e = Between.create(middle, min, max, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Between.create(min, min, max, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Between.create(max, min, max, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Between.create(min, middle, max, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Between.create(min, middle, max, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testBetweenIncomplete() {
        Errors err = new Errors();
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Between.create(c, c, new Field("integer"), err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Between.create(c, new Field("integer"), c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Between.create(new Field("integer"), c, c, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testBetweenBind() {
        Errors err = new Errors();
        Expression c1 = Constant.create(12, DataType.INTEGER);
        Expression c2 = Constant.create(54, DataType.INTEGER);
        Field f = new Field("integer");

        Expression e = Between.create(f, c1, c2, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Between.create(c1, f, c2, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Between.create(c1, c2, f, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testIs() {
        Errors err = new Errors();
        Expression e;

        e = Is.create(Constant.TRUE, LogicValue.TRUE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Is.create(Constant.TRUE, LogicValue.FALSE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.TRUE, LogicValue.UNKNOWN, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.FALSE, LogicValue.TRUE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.FALSE, LogicValue.FALSE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Is.create(Constant.FALSE, LogicValue.UNKNOWN, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.UNKNOWN, LogicValue.TRUE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.UNKNOWN, LogicValue.FALSE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.UNKNOWN, LogicValue.UNKNOWN, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Is.create(Constant.NULL, LogicValue.TRUE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = Is.create(Constant.NULL, LogicValue.FALSE, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = Is.create(Constant.NULL, LogicValue.UNKNOWN, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsIncomplete() {
        Errors err = new Errors();
        Expression e = Is.create(new Field("boolean"), LogicValue.FALSE, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsBind() {
        Errors err = new Errors();
        Expression f = new Field("boolean");

        Expression e = Is.create(f, LogicValue.TRUE, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testIsNull() {
        Errors err = new Errors();
        Expression e;

        e = IsNull.create(Constant.NULL);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.INTEGER_0);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = IsNull.create(Constant.FALSE);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = IsNull.create(Constant.UNKNOWN);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testIsNullIncomplete() {
        Expression e = IsNull.create(new Field("boolean"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testIsNullBind() {
        Errors err = new Errors();
        Expression f = new Field("integer");

        Expression e = IsNull.create(f);
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
    public void testLike() {
        Errors err = new Errors();
        Expression c;
        Expression e;

        c = Constant.create("test", DataType.STRING);
        e = Like.create(c, "_est", err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        c = Constant.create("interest", DataType.STRING);
        e = Like.create(c, "inte[s-y]est", err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLikeIncomplete() {
        Errors err = new Errors();
        Expression e = Like.create(new Field("string"), "test", err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNullLike() {
        Errors err = new Errors();
        Expression e = Like.create(Constant.NULL, "test", err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNullBind() {
        Errors err = new Errors();
        Expression f = new Field("string");

        Expression e = Like.create(f, "test", err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(stringAtt));
        assertTrue(e.isComplete());
    }

}
