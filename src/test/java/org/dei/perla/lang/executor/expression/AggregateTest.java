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
import static org.junit.Assert.*;

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

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        });
    }

    private static final Expression tsExpr =
            new Field(Attribute.TIMESTAMP.getId());
    private static final Expression intExpr =
            new Field(intAtt.getId());
    private static final Expression floatExpr =
            new Field(floatAtt.getId());

    private static BufferView intView;
    private static BufferView floatView;
    private static BufferView tsView;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                intAtt,
                stringAtt,
                floatAtt
        };

        Buffer b = new ArrayBuffer(1, 512);
        b.add(new Record(atts, new Object[]{0, Instant.now()}));
        b.add(new Record(atts, new Object[]{1, Instant.now()}));
        b.add(new Record(atts, new Object[]{2, Instant.now()}));
        b.add(new Record(atts, new Object[]{3, Instant.now()}));
        b.add(new Record(atts, new Object[]{4, Instant.now()}));
        intView = b.unmodifiableView();

        b = new ArrayBuffer(1, 512);
        b.add(new Record(atts, new Object[]{0.0f, Instant.now()}));
        b.add(new Record(atts, new Object[]{1.1f, Instant.now()}));
        b.add(new Record(atts, new Object[]{2.2f, Instant.now()}));
        b.add(new Record(atts, new Object[]{3.3f, Instant.now()}));
        b.add(new Record(atts, new Object[]{4.4f, Instant.now()}));
        floatView = b.unmodifiableView();

        b = new ArrayBuffer(0, 512);
        b.add(new Record(atts, new Object[]{Instant.now()}));
        b.add(new Record(atts, new Object[]{Instant.now()}));
        b.add(new Record(atts, new Object[]{Instant.now()}));
        b.add(new Record(atts, new Object[]{Instant.now()}));
        b.add(new Record(atts, new Object[]{Instant.now()}));
        tsView = b.unmodifiableView();
    }

    @Test
    public void testIntegerSum() {
        Expression e = Aggregate.createSum(intExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(10));

        e = Aggregate.createSum (intExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(9));

        Expression filter = Comparison.createGT(intExpr,
                Constant.create(3, DataType.INTEGER));
        e = Aggregate.createSum(intExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatSum() {
        Expression e = Aggregate.createSum(floatExpr,
                new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f));

        e = Aggregate.createSum(floatExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(9.9f));

        Expression filter = Comparison.createGT(floatExpr,
                Constant.create(2.2f, DataType.FLOAT));
        e = Aggregate.createSum(floatExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertThat(res, equalTo(7.7f));
    }

    @Test
    public void testSumNull() {
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createSum(nuli, new WindowSize(3), null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createSum(nuli, new WindowSize(3), nulb);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createSum(intExpr, new WindowSize(3), null);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testSumError() {
        WindowSize ws = new WindowSize(10);
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Aggregate.createSum(err, ws, null);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createSum(c, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createSum(err, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testSumBind() {
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createSum(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertFalse(e.hasErrors());
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createSum(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createSum(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createSum(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testIntegerAvg() {
        Expression e = Aggregate.createAvg(intExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2f));

        e = Aggregate.createAvg(intExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3f));

        Expression filter = Comparison.createGT(intExpr,
                Constant.create(2, DataType.INTEGER));
        e = (AvgAggregate) Aggregate.createAvg(intExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(7f / 2));
    }

    @Test
    public void testFloatAvg() {
        Expression e = Aggregate.createAvg(floatExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f / 5));

        e = (AvgAggregate) Aggregate.createAvg(floatExpr, new WindowSize(3), null);
        e = e.bind(atts);
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3.3f));

        Expression filter = Comparison.createGT(floatExpr,
                Constant.create(2.2f, DataType.FLOAT));
        e = Aggregate.createAvg(floatExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        res = e.run(null, floatView);
        assertThat(res, equalTo(7.7f / 2));
    }

    @Test
    public void testAvgNull() {
        Expression nulf = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createAvg(nulf, new WindowSize(3), null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createAvg(nulf, new WindowSize(3), nulb);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createAvg(intExpr, new WindowSize(3), null);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testAvgError() {
        WindowSize ws = new WindowSize(10);
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Aggregate.createAvg(err, ws, null);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createAvg(c, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createAvg(err, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testAvgBind() {
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createAvg(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createAvg(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createAvg(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createAvg(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testIntegerMin() {
        Expression e = Aggregate.createMin(intExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(0));

        e = Aggregate.createMin(intExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(2));

        Expression filter = Comparison.createGT(intExpr,
                Constant.create(3, DataType.INTEGER));
        e = Aggregate.createMin(intExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatMin() {
        Expression e = Aggregate.createMin(floatExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(0f));

        e = Aggregate.createMin(floatExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2.2f));

        Expression filter = Comparison.createGT(floatExpr,
                Constant.create(2.2f, DataType.FLOAT));
        e = Aggregate.createMin(floatExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMin() {
        Expression e = Aggregate.createMin(tsExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(4)[0]));

        e = Aggregate.createMin(tsExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(2)[0]));
    }

    @Test
    public void testMinNull() {
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createMin(nuli, new WindowSize(3), null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertThat(res, nullValue());

        e = Aggregate.createMin(nuli, new WindowSize(3), nulb);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, nullValue());
    }

    @Test
    public void testMinError() {
        WindowSize ws = new WindowSize(10);
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Aggregate.createMin(err, ws, null);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createMin(c, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createMin(err, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testMinBind() {
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createMin(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createMin(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createMin(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createMin(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testIntegerMax() {
        Expression e = Aggregate.createMax(intExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        e = Aggregate.createMax(intExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        Expression filter = Comparison.createLT(intExpr,
                Constant.create(3, DataType.INTEGER));
        e = Aggregate.createMax(intExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(2));
    }

    @Test
    public void testFloatMax() {
        Expression e = Aggregate.createMax(floatExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        e = Aggregate.createMax(floatExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        Expression filter = Comparison.createLT(floatExpr,
                Constant.create(4.4f, DataType.FLOAT));
        e = Aggregate.createMax(floatExpr, new WindowSize(5), filter);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMax() {
        Expression e = Aggregate.createMax(tsExpr, new WindowSize(5), null);
        e = e.bind(atts);
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(0)[0]));

        e = Aggregate.createMax(tsExpr, new WindowSize(3), null);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(0)[0]));
    }

    @Test
    public void testMaxNull() {
        Expression nulf = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createMax(nulf, new WindowSize(3), null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertThat(res, nullValue());

        e = Aggregate.createMax(nulf, new WindowSize(3), nulb);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, nullValue());
    }

    @Test
    public void testMaxError() {
        WindowSize ws = new WindowSize(10);
        Expression err = new ErrorExpression("test");
        Expression c = Constant.create(85, DataType.INTEGER);

        Expression e = Aggregate.createMax(err, ws, null);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createMax(c, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());

        e = Aggregate.createMax(err, ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testMaxBind() {
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createMax(exp, new WindowSize(3), null);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createMax(exp, new WindowSize(3), cFilt);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createMax(exp, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());

        e = Aggregate.createMax(e, new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

    @Test
    public void testCount() {
        Expression e = Aggregate.createCount(new WindowSize(5), null);
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(5));

        e = Aggregate.createCount(new WindowSize(3), null);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, floatView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(3));

        Expression filter = Comparison.createLT(intExpr,
                Constant.create(3, DataType.INTEGER));
        e = Aggregate.createCount(new WindowSize(5), filter);
        e = e.bind(atts);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        res = e.run(null, intView);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testCountNull() {
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createCount(new WindowSize(3), nulb);
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
        Object res = e.run(null, intView);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testCountError() {
        WindowSize ws = new WindowSize(10);
        Expression err = new ErrorExpression("test");

        Expression e = Aggregate.createCount(ws, err);
        assertTrue(e.isComplete());
        assertTrue(e.hasErrors());
    }

    @Test
    public void testCountBind() {
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c);

        Expression e = Aggregate.createCount(new WindowSize(3), filter);
        assertFalse(e.isComplete());
        assertFalse(e.hasErrors());
        e = e.bind(atts);
        List<Attribute> as = e.getAttributes();
        assertThat(as.size(), equalTo(1));
        assertTrue(as.contains(intAtt));
        assertTrue(e.isComplete());
        assertFalse(e.hasErrors());
    }

}
