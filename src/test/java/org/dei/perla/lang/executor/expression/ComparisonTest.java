package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
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
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createEQ(new Field("integer"), c);
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] record = new Object[]{4};
        Object res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{3};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createEQ(new Field("float"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{4.4f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{3.3f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create("4", DataType.STRING);
        e = Comparison.createEQ(new Field("string"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{"4"};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{"3"};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createEQ(trueExpr, trueExpr).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createEQ(trueExpr, falseExpr).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createEQ(t1, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createEQ(t1, t2).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testEQIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createEQ(c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createEQ(new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testEQNull() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createEQ(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createEQ(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createEQ(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testEQError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Comparison.createEQ(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createEQ(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createEQ(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testEQBind() {
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createEQ(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createEQ(c, f);
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createEQ(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testNE() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createNE(new Field("integer"), c);
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object[] record = new Object[]{4};
        Object res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{3};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createNE(new Field("float"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{4.4f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{3.3f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create("4", DataType.STRING);
        e = Comparison.createNE(new Field("string"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{"4"};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{"3"};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createNE(trueExpr, trueExpr).run(null, null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createNE(trueExpr, falseExpr).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createNE(t1, t1).run(null, null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createNE(t1, t2).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testNEIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createNE(c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createNE(new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNENull() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createNE(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createNE(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createNE(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNEError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Comparison.createNE(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createNE(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createNE(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testNEBind() {
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createNE(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createNE(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createNE(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testGT() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createGT(c, new Field("integer"));
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] record = new Object[]{4};
        Object res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{3};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGT(new Field("integer"), c);
        bound.clear();
        e = e.bind(atts, bound);
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createGT(c, new Field("float"));
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{4.4f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{3.3f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGT(new Field("float"), c);
        bound.clear();
        e = e.bind(atts, bound);
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createGT(t1, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createGT(t1, t2).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createGT(t2, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testGTIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createGT(c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGT(new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGTNull() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createGT(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGT(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGT(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGTError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Comparison.createGT(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createGT(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createGT(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testGTBind() {
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createGT(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGT(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGT(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testGE() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createGE(c, new Field("integer"));
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] record = new Object[]{4};
        Object res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{3};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGE(new Field("integer"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createGE(c, new Field("float"));
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{4.4f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{3.3f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        e = Comparison.createGE(new Field("float"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));

        res = Comparison.createGE(t1, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createGE(t1, t2).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createGE(t2, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
    }

    @Test
    public void testGEIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createGE(c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGE(new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGENull() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createGE(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGE(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createGE(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testGEError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Comparison.createGE(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createGE(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createGE(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testGEBind() {
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createGE(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGE(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGE(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testLT() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createLT(c, new Field("integer"));
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] record = new Object[]{4};
        Object res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{3};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLT(new Field("integer"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createLT(c, new Field("float"));
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{4.4f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        record = new Object[]{3.3f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLT(new Field("float"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createLT(t1, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        res = Comparison.createLT(t1, t2).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createLT(t2, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLTIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createLT(c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLT(new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLTNull() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createLT(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLT(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLT(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLTError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Comparison.createLT(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createLT(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createLT(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testLTBind() {
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createLT(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLT(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLT(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testLE() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression e = Comparison.createLE(c, new Field("integer"));
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object[] record = new Object[]{4};
        Object res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{3};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLE(new Field("integer"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        c = Constant.create(4.4f, DataType.FLOAT);
        e = Comparison.createLE(c, new Field("float"));
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        record = new Object[]{4.4f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        record = new Object[]{3.3f};
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.FALSE));
        e = Comparison.createLE(new Field("float"), c);
        bound.clear();
        e = e.bind(atts, bound);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(record, null);
        assertThat(res, equalTo(LogicValue.TRUE));

        res = Comparison.createLE(t1, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createLE(t1, t2).run(null, null);
        assertThat(res, equalTo(LogicValue.TRUE));
        res = Comparison.createLE(t2, t1).run(null, null);
        assertThat(res, equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLEIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Comparison.createLE(c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLE(new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLENull() {
        Expression c = Constant.create(4, DataType.INTEGER);
        Expression nul = Constant.NULL;

        Expression e = Comparison.createLE(c, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLE(nul, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Comparison.createLE(nul, nul);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testLEError() {
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Comparison.createLE(err, c);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createLE(c, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Comparison.createLE(err, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testLEBind() {
        Expression f = new Field("integer");
        Expression c = Constant.create(12, DataType.INTEGER);

        Expression e = Comparison.createLE(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLE(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLE(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        bound.clear();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testBetween() {
        Expression min = Constant.create(-0.5f, DataType.FLOAT);
        Expression max = Constant.create(456f, DataType.FLOAT);
        Expression middle = Constant.create(23f, DataType.FLOAT);

        Expression e = Between.create(middle, min, max);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Between.create(min, min, max);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Between.create(max, min, max);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Between.create(min, middle, max);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Between.create(min, middle, max);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testBetweenIncomplete() {
        Expression c = Constant.create(4, DataType.INTEGER);

        Expression e = Between.create(c, c, new Field("integer"));
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Between.create(c, new Field("integer"), c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));

        e = Between.create(new Field("integer"), c, c);
        assertFalse(e.isComplete());
        res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testBetweenError() {
        Expression c1 = Constant.create(-0.5f, DataType.FLOAT);
        Expression c2 = Constant.create(456f, DataType.FLOAT);
        Expression err = new ErrorExpression("test");

        Expression e = Between.create(err, c1, c2);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Between.create(c1, err, c2);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Between.create(c1, c2, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testBetweenBind() {
        Expression c1 = Constant.create(12, DataType.INTEGER);
        Expression c2 = Constant.create(54, DataType.INTEGER);
        Field f = new Field("integer");

        Expression e = Between.create(f, c1, c2);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Between.create(c1, f, c2);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Between.create(c1, c2, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testIs() {
        Expression e;

        e = Is.create(Constant.TRUE, LogicValue.TRUE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Is.create(Constant.TRUE, LogicValue.FALSE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.TRUE, LogicValue.UNKNOWN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.FALSE, LogicValue.TRUE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.FALSE, LogicValue.FALSE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Is.create(Constant.FALSE, LogicValue.UNKNOWN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.UNKNOWN, LogicValue.TRUE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.UNKNOWN, LogicValue.FALSE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = Is.create(Constant.UNKNOWN, LogicValue.UNKNOWN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = Is.create(Constant.NULL, LogicValue.TRUE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = Is.create(Constant.NULL, LogicValue.FALSE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));

        e = Is.create(Constant.NULL, LogicValue.UNKNOWN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsIncomplete() {
        Expression e = Is.create(new Field("boolean"), LogicValue.FALSE);
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testIsBind() {
        Expression f = new Field("boolean");

        Expression e = Is.create(f, LogicValue.TRUE);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(boolAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testIsNull() {
        Expression e;

        e = IsNull.create(Constant.NULL);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.NULL);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.NULL);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.NULL);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.NULL);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.NULL);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        e = IsNull.create(Constant.INTEGER_0);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = IsNull.create(Constant.FALSE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));

        e = IsNull.create(Constant.UNKNOWN);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
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
        Expression f = new Field("integer");

        Expression e = IsNull.create(f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testLike() {
        Expression c;
        Expression e;

        c = Constant.create("test", DataType.STRING);
        e = Like.create(c, "_est");
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.TRUE));

        c = Constant.create("interest", DataType.STRING);
        e = Like.create(c, "inte[s-y]est");
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.FALSE));
    }

    @Test
    public void testLikeIncomplete() {
        Expression e = Like.create(new Field("string"), "test");
        assertFalse(e.isComplete());
        Object res = e.run(null, null);
        assertThat(res, equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNullLike() {
        Expression e = Like.create(Constant.NULL, "test");
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertThat(e.run(null, null), equalTo(LogicValue.UNKNOWN));
    }

    @Test
    public void testNullBind() {
        Expression f = new Field("string");

        Expression e = Like.create(f, "test");
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(stringAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

}
