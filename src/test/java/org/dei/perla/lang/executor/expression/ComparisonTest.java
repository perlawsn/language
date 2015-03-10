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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 23/02/15.
 */
public class ComparisonTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static final Expression tsExpr = new Field(0, DataType.TIMESTAMP);
    private static final Expression intExpr = new Field(1, DataType.INTEGER);
    private static final Expression stringExpr = new Field(2, DataType.STRING);
    private static final Expression floatExpr = new Field(3, DataType.FLOAT);

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
                integerAtt,
                stringAtt,
                floatAtt
        };
        List<Attribute> atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void equalTest() {
        Expression eq = Comparison.createEQ(intExpr, new Constant(4, DataType.INTEGER));
        assertTrue(eq.isComplete());
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = Comparison.createEQ(floatExpr, new Constant(4.4f, DataType.FLOAT));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = Comparison.createEQ(stringExpr, new Constant("4", DataType.STRING));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
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
    public void notEqualTest() {
        Expression eq = Comparison.createNE(intExpr, new Constant(4, DataType.INTEGER));
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        assertTrue(eq.isComplete());
        Object res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = Comparison.createNE(floatExpr, new Constant(4.4f, DataType.FLOAT));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = Comparison.createNE(stringExpr, new Constant("4", DataType.STRING));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        res = Comparison.createNE(trueExpr, trueExpr).run(null, null);
        assertTrue(eq.isComplete());
        assertThat(res, equalTo(false));
        res = Comparison.createNE(trueExpr, falseExpr).run(null, null);
        assertThat(res, equalTo(true));

        res = Comparison.createNE(t1, t1).run(null, null);
        assertTrue(eq.isComplete());
        assertThat(res, equalTo(false));
        res = Comparison.createNE(t1, t2).run(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void greaterTest() {
        Expression eq = Comparison.createGT(new Constant(4, DataType.INTEGER), intExpr);
        assertTrue(eq.isComplete());
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = Comparison.createGT(intExpr, new Constant(4, DataType.INTEGER));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = Comparison.createGT(new Constant(4.4f, DataType.FLOAT), floatExpr);
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = Comparison.createGT(floatExpr, new Constant(4.4f, DataType.FLOAT));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));

        res = Comparison.createGT(t1, t1).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createGT(t1, t2).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createGT(t2, t1).run(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void greaterEqualTest() {
        Expression eq = Comparison.createGE(new Constant(4, DataType.INTEGER), intExpr);
        assertTrue(eq.isComplete());
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = Comparison.createGE(intExpr, new Constant(4, DataType.INTEGER));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));

        eq = Comparison.createGE(new Constant(4.4f, DataType.FLOAT), floatExpr);
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));
        eq = Comparison.createGE(floatExpr, new Constant(4.4f, DataType.FLOAT));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));

        res = Comparison.createGE(t1, t1).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createGE(t1, t2).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createGE(t2, t1).run(null, null);
        assertThat(res, equalTo(true));
    }

    @Test
    public void lessTest() {
        Expression eq = Comparison.createLT(new Constant(4, DataType.INTEGER), intExpr);
        assertTrue(eq.isComplete());
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = Comparison.createLT(intExpr, new Constant(4, DataType.INTEGER));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = Comparison.createLT(new Constant(4.4f, DataType.FLOAT), floatExpr);
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(false));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = Comparison.createLT(floatExpr, new Constant(4.4f, DataType.FLOAT));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        res = Comparison.createLT(t1, t1).run(null, null);
        assertThat(res, equalTo(false));
        res = Comparison.createLT(t1, t2).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createLT(t2, t1).run(null, null);
        assertThat(res, equalTo(false));
    }

    @Test
    public void lessEqualTest() {
        Expression eq = Comparison.createLE(new Constant(4, DataType.INTEGER), intExpr);
        assertTrue(eq.isComplete());
        assertThat(eq.getType(), equalTo(DataType.BOOLEAN));
        Object res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = Comparison.createLE(intExpr, new Constant(4, DataType.INTEGER));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        eq = Comparison.createLE(new Constant(4.4f, DataType.FLOAT), floatExpr);
        assertTrue(eq.isComplete());
        res = eq.run(view.get(0), view);
        assertThat(res, equalTo(true));
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(false));
        eq = Comparison.createLE(floatExpr, new Constant(4.4f, DataType.FLOAT));
        assertTrue(eq.isComplete());
        res = eq.run(view.get(1), view);
        assertThat(res, equalTo(true));

        res = Comparison.createLE(t1, t1).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createLE(t1, t2).run(null, null);
        assertThat(res, equalTo(true));
        res = Comparison.createLE(t2, t1).run(null, null);
        assertThat(res, equalTo(false));
    }

}
