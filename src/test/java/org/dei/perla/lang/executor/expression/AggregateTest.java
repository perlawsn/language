package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;
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
 * @author Guido Rota 02/03/15.
 */
public class AggregateTest {

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static List<Attribute> atts;

    private static Expression tsExpr;
    private static Expression intExpr;
    private static Expression floatExpr;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        };
        atts = Arrays.asList(as);

        tsExpr = new Field(Attribute.TIMESTAMP.getId()).rebuild(atts);
        intExpr = new Field(intAtt.getId()).rebuild(atts);
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
    public void testIntegerSum() {
        Expression e = Aggregate.createSum(intExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(10));

        e = Aggregate.createSum (intExpr, new WindowSize(3), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(9));

        Expression filter = Comparison.createGT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        e = Aggregate.createSum(intExpr, new WindowSize(5), filter);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(7));
    }

    @Test
    public void testFloatSum() {
        Expression e = Aggregate.createSum(floatExpr, new
                WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f));

        e = Aggregate.createSum(floatExpr, new WindowSize(3), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(9.9f));

        Expression filter = Comparison.createGT(intExpr,
                new Constant(2, DataType.INTEGER));
        e = Aggregate.createSum(floatExpr, new WindowSize(5), filter);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(7.7f));
    }

    @Test
    public void testSumNull() {
        Expression e = Aggregate.createSum(Null.INSTANCE, new WindowSize(3),
                null);
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertThat(res, nullValue());

        e = Aggregate.createSum(Null.INSTANCE, new WindowSize(3),
                Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, nullValue());
    }

    @Test
    public void testSumRebuild() {
        Expression c = new Constant(12, DataType.INTEGER);
        Expression cFilt = new Constant(true, DataType.BOOLEAN);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createSum(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createSum(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createSum(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createSum(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void testIntegerAvg() {
        Expression e = Aggregate.createAvg(intExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2f));

        e = Aggregate.createAvg(intExpr, new WindowSize(3), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3f));

        Expression filter = Comparison.createGT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        e = (AvgAggregate) Aggregate.createAvg(intExpr, new WindowSize(5), filter);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(7f / 2));
    }

    @Test
    public void testFloatAvg() {
        Expression e = Aggregate.createAvg(floatExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f / 5));

        e = (AvgAggregate) Aggregate.createAvg(floatExpr, new WindowSize(3), null);
        res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3.3f));

        Expression filter = Comparison.createGT(intExpr,
                new Constant(2, DataType.INTEGER));
        e = Aggregate.createAvg(floatExpr, new WindowSize(5), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(7.7f / 2));
    }

    @Test
    public void testAvgNull() {
        Expression e = Aggregate.createAvg(Null.INSTANCE, new WindowSize(3),
                null);
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertThat(res, nullValue());

        e = Aggregate.createAvg(Null.INSTANCE, new WindowSize(3),
                Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, nullValue());
    }

    @Test
    public void testAvgRebuild() {
        Expression c = new Constant(12, DataType.INTEGER);
        Expression cFilt = new Constant(true, DataType.BOOLEAN);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createAvg(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createAvg(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createAvg(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createAvg(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void testIntegerMin() {
        Expression e = Aggregate.createMin(intExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(0));

        e = Aggregate.createMin(intExpr, new WindowSize(3), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(2));

        Expression filter = Comparison.createGT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        e = Aggregate.createMin(intExpr, new WindowSize(5), filter);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testFloatMin() {
        Expression e = Aggregate.createMin(floatExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(0f));

        e = Aggregate.createMin(floatExpr, new WindowSize(3), null);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2.2f));

        Expression filter = Comparison.createGT(intExpr,
                new Constant(2, DataType.INTEGER));
        e = Aggregate.createMin(floatExpr, new WindowSize(5), filter);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMin() {
        Expression e = Aggregate.createMin(tsExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(4)[0]));

        e = Aggregate.createMin(tsExpr, new WindowSize(3), null);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(2)[0]));

        Expression filter = Comparison.createLT(intExpr, new Constant(4, DataType.INTEGER));
        e = Aggregate.createMin(tsExpr, new WindowSize(5), filter);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(view.get(4)[0]));
    }

    @Test
    public void testMinNull() {
        Expression e = Aggregate.createMin(Null.INSTANCE, new WindowSize(3),
                null);
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertThat(res, nullValue());

        e = Aggregate.createMin(Null.INSTANCE, new WindowSize(3),
                Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, nullValue());
    }

    @Test
    public void testMinRebuild() {
        Expression c = new Constant(12, DataType.INTEGER);
        Expression cFilt = new Constant(true, DataType.BOOLEAN);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createMin(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createMin(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createMin(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createMin(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void testIntegerMax() {
        Expression e = Aggregate.createMax(intExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        e = Aggregate.createMax(intExpr, new WindowSize(3), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        Expression filter = Comparison.createLT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        e = Aggregate.createMax(intExpr, new WindowSize(5), filter);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(2));
    }

    @Test
    public void testFloatMax() {
        Expression e = Aggregate.createMax(floatExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        e = Aggregate.createMax(floatExpr, new WindowSize(3), null);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(4, DataType.INTEGER));
        e = Aggregate.createMax(floatExpr, new WindowSize(5), filter);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMax() {
        Expression e = Aggregate.createMax(tsExpr, new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(0)[0]));

        e = Aggregate.createMax(tsExpr, new WindowSize(3), null);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(0)[0]));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(4, DataType.INTEGER));
        e = Aggregate.createMax(tsExpr, new WindowSize(5), filter);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(view.get(1)[0]));
    }

    @Test
    public void testMaxNull() {
        Expression e = Aggregate.createMax(Null.INSTANCE, new WindowSize(3),
                null);
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertThat(res, nullValue());

        e = Aggregate.createMax(Null.INSTANCE, new WindowSize(3),
                Null.INSTANCE);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, nullValue());
    }

    @Test
    public void testMaxRebuild() {
        Expression c = new Constant(12, DataType.INTEGER);
        Expression cFilt = new Constant(true, DataType.BOOLEAN);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createMax(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createMax(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createMax(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());

        e = Aggregate.createMax(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

    @Test
    public void testCount() {
        Expression e = Aggregate.createCount(new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(5));

        e = Aggregate.createCount(new WindowSize(3), null);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(3));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(3, DataType.INTEGER));
        e = Aggregate.createCount(new WindowSize(5), filter);
        assertTrue(e.isComplete());
        res = e.run(null, view);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testCountNull() {
        Expression e = Aggregate.createCount(new WindowSize(3), Null.INSTANCE);
        assertTrue(e.isComplete());
        Object res = e.run(null, view);
        assertThat(res, nullValue());
    }

    @Test
    public void testCountRebuild() {
        Expression c = new Constant(12, DataType.INTEGER);
        Expression cFilt = new Constant(true, DataType.BOOLEAN);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createCount(new WindowSize(3), filter);
        assertFalse(e.isComplete());
        e = e.rebuild(atts);
        assertTrue(e.isComplete());
    }

}
