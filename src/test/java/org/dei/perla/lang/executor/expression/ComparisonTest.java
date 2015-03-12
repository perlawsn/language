package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

    private static BufferView view;

    private static List<Attribute> atts;

    private static Expression intExpr;
    private static Expression stringExpr;
    private static Expression floatExpr;

    private static final Expression trueExpr = new Constant(true, DataType.BOOLEAN);
    private static final Expression falseExpr = new Constant(false, DataType.BOOLEAN);

    private static final Expression t1 =
            new Constant(Instant.parse("2015-02-23T15:07:45.000Z"), DataType.TIMESTAMP);
    private static final Expression t2 =
            new Constant(Instant.parse("2015-02-23T15:08:45.000Z"), DataType.TIMESTAMP);

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        };
        atts = Arrays.asList(as);

        intExpr = new Field(intAtt.getId()).rebuild(atts);
        stringExpr = new Field(stringAtt.getId()).rebuild(atts);
        floatExpr = new Field(floatAtt.getId()).rebuild(atts);

        Buffer b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void testEQ() {
        Expression c = new Constant(4, DataType.INTEGER);
        Expression e = Comparison.createEQ(intExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        c = new Constant(4.4f, DataType.FLOAT);
        e = Comparison.createEQ(floatExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        c = new Constant("4", DataType.STRING);
        e = Comparison.createEQ(stringExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        res = Comparison.createEQ(trueExpr, trueExpr).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createEQ(trueExpr, falseExpr).run(null, null);
        assertThat(res, equalTo(false));

        res = Comparison.createEQ(t1, t1).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createEQ(t1, t2).run(null, null);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testEQNull() {
        Expression c = new Constant(4, DataType.INTEGER);

        Expression e = Comparison.createEQ(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createEQ(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createEQ(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testEQError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

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
    public void testEQRebuild() {
        Expression f = new Field("integer");
        Constant c = new Constant(12, DataType.INTEGER);

        Expression e = Comparison.createEQ(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createEQ(c, f);
        assertFalse(e.hasErrors());
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createEQ(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testNE() {
        Constant c = new Constant(4, DataType.INTEGER);
        Expression e = Comparison.createNE(intExpr, c);
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        c = new Constant(4.4f, DataType.FLOAT);
        e = Comparison.createNE(floatExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        c = new Constant("4", DataType.STRING);
        e = Comparison.createNE(stringExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        res = Comparison.createNE(trueExpr, trueExpr).run(null, null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(res, equalTo(false));
        res = Comparison.createNE(trueExpr, falseExpr).run(null, null);
        assertThat(res, equalTo(true));

        res = Comparison.createNE(t1, t1).run(null, null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(res, equalTo(false));
        res = Comparison.createNE(t1, t2).run(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testNENull() {
        Expression c = new Constant(4, DataType.INTEGER);

        Expression e = Comparison.createNE(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createNE(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createNE(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testNEError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

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
    public void testNERebuild() {
        Expression f = new Field("integer");
        Constant c = new Constant(12, DataType.INTEGER);

        Expression e = Comparison.createNE(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createNE(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createNE(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testGT() {
        Constant c = new Constant(4, DataType.INTEGER);
        Expression e = Comparison.createGT(c, intExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));
        e = Comparison.createGT(intExpr, c);
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        c = new Constant(4.4f, DataType.FLOAT);
        e = Comparison.createGT(c, floatExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));
        e = Comparison.createGT(floatExpr, c);
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        res = Comparison.createGT(t1, t1).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createGT(t1, t2).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createGT(t2, t1).run(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testGTNull() {
        Expression c = new Constant(4, DataType.INTEGER);

        Expression e = Comparison.createGT(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createGT(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createGT(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testGTError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

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
    public void testGTRebuild() {
        Expression f = new Field("integer");
        Constant c = new Constant(12, DataType.INTEGER);

        Expression e = Comparison.createGT(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGT(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGT(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testGE() {
        Constant c = new Constant(4, DataType.INTEGER);
        Expression e = Comparison.createGE(c, intExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));
        e = Comparison.createGE(intExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        c = new Constant(4.4f, DataType.FLOAT);
        e = Comparison.createGE(c, floatExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));
        e = Comparison.createGE(floatExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));

        res = Comparison.createGE(t1, t1).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createGE(t1, t2).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createGE(t2, t1).run(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void testGENull() {
        Expression c = new Constant(4, DataType.INTEGER);

        Expression e = Comparison.createGE(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createGE(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createGE(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testGEError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

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
    public void testGERebuild() {
        Expression f = new Field("integer");
        Constant c = new Constant(12, DataType.INTEGER);

        Expression e = Comparison.createGE(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGE(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createGE(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testLT() {
        Constant c = new Constant(4, DataType.INTEGER);
        Expression e = Comparison.createLT(c, intExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));
        e = Comparison.createLT(intExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        c = new Constant(4.4f, DataType.FLOAT);
        e = Comparison.createLT(c, floatExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));
        e = Comparison.createLT(floatExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        res = Comparison.createLT(t1, t1).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createLT(t1, t2).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createLT(t2, t1).run(null, null);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testLTNull() {
        Expression c = new Constant(4, DataType.INTEGER);

        Expression e = Comparison.createLT(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createLT(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createLT(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testLTError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

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
    public void testLTRebuild() {
        Expression f = new Field("integer");
        Constant c = new Constant(12, DataType.INTEGER);

        Expression e = Comparison.createLT(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLT(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLT(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testLE() {
        Constant c = new Constant(4, DataType.INTEGER);
        Expression e = Comparison.createLE(c, intExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        assertThat(e.getType(), equalTo(DataType.BOOLEAN));
        Object res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));
        e = Comparison.createLE(intExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        c = new Constant(4.4f, DataType.FLOAT);
        e = Comparison.createLE(c, floatExpr);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(false));
        e = Comparison.createLE(floatExpr, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(view.get(1), view);
        assertThat(res, equalTo(true));

        res = Comparison.createLE(t1, t1).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createLE(t1, t2).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createLE(t2, t1).run(null, null);
        assertThat(res, equalTo(false));
    }

    @Test
    public void testLENull() {
        Expression c = new Constant(4, DataType.INTEGER);

        Expression e = Comparison.createLE(c, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createLE(Null.INSTANCE, c);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());

        e = Comparison.createLE(Null.INSTANCE, Null.INSTANCE);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, null);
        assertThat(res, nullValue());
    }

    @Test
    public void testLEError() {
        Expression err = new ErrorExpression("test");
        Expression c = new Constant(85, DataType.INTEGER);

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
    public void testLERebuild() {
        Expression f = new Field("integer");
        Constant c = new Constant(12, DataType.INTEGER);

        Expression e = Comparison.createLE(f, c);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLE(c, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Comparison.createLE(f, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testBetween() {
        Constant min = new Constant(-0.5f, DataType.FLOAT);
        Constant max = new Constant(456f, DataType.FLOAT);
        Constant middle = new Constant(23f, DataType.FLOAT);

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
    public void testBetweenError() {
        Constant c1 = new Constant(-0.5f, DataType.FLOAT);
        Constant c2 = new Constant(456f, DataType.FLOAT);
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
    public void testBetweenRebuild() {
        Constant c1 = new Constant(12, DataType.INTEGER);
        Constant c2 = new Constant(54, DataType.INTEGER);
        Field f = new Field("integer");

        Expression e = Between.create(f, c1, c2);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Between.create(c1, f, c2);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Between.create(c1, c2, f);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

}
