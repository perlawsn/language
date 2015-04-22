package org.dei.perla.lang.executor.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.statement.WindowSize;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
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
        List<Attribute> bufAtts = Arrays.asList(new Attribute[]{
                intAtt,
                Attribute.TIMESTAMP
        });
        Buffer b = new ArrayBuffer(bufAtts, 512);
        b.add(new Object[]{0, Instant.now()});
        b.add(new Object[]{1, Instant.now()});
        b.add(new Object[]{2, Instant.now()});
        b.add(new Object[]{3, Instant.now()});
        b.add(new Object[]{4, Instant.now()});
        intView = b.unmodifiableView();

        bufAtts = Arrays.asList(new Attribute[]{
                floatAtt,
                Attribute.TIMESTAMP
        });
        b = new ArrayBuffer(bufAtts, 512);
        b.add(new Object[]{0.0f, Instant.now()});
        b.add(new Object[]{1.1f, Instant.now()});
        b.add(new Object[]{2.2f, Instant.now()});
        b.add(new Object[]{3.3f, Instant.now()});
        b.add(new Object[]{4.4f, Instant.now()});
        floatView = b.unmodifiableView();

        bufAtts = Arrays.asList(new Attribute[]{
                Attribute.TIMESTAMP
        });
        b = new ArrayBuffer(bufAtts, 512);
        b.add(new Object[]{Instant.now()});
        b.add(new Object[]{Instant.now()});
        b.add(new Object[]{Instant.now()});
        b.add(new Object[]{Instant.now()});
        b.add(new Object[]{Instant.now()});
        tsView = b.unmodifiableView();
    }

    @Test
    public void testIntegerSum() {
        Errors err = new Errors();
        Expression e = Aggregate.createSum(intExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(10));

        e = Aggregate.createSum (intExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(9));

        Expression filter = Comparison.createGT(intExpr,
                Constant.create(3, DataType.INTEGER), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createSum(intExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatSum() {
        Errors err = new Errors();
        Expression e = Aggregate.createSum(floatExpr,
                new WindowSize(5), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f));

        e = Aggregate.createSum(floatExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(9.9f));

        Expression filter = Comparison.createGT(floatExpr,
                Constant.create(2.2f, DataType.FLOAT), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createSum(floatExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertThat(res, equalTo(7.7f));
    }

    @Test
    public void testSumNull() {
        Errors err = new Errors();
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createSum(nuli, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(err.isEmpty());
        Object res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createSum(nuli, new WindowSize(3), nulb, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        assertTrue(err.isEmpty());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createSum(intExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        assertTrue(err.isEmpty());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testSumBind() {
        Errors err = new Errors();
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c, err);
        assertTrue(err.isEmpty());

        Expression e = Aggregate.createSum(exp, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createSum(exp, new WindowSize(3), cFilt, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createSum(exp, new WindowSize(3), filter, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createSum(exp, new WindowSize(3), filter, err);
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
    public void testIntegerAvg() {
        Errors err = new Errors();
        Expression e = Aggregate.createAvg(intExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2f));

        e = Aggregate.createAvg(intExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3f));

        Expression filter = Comparison.createGT(intExpr,
                Constant.create(2, DataType.INTEGER), err);
        assertTrue(err.isEmpty());
        e = (AvgAggregate) Aggregate.createAvg(intExpr, new WindowSize(5),
                filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(7f / 2));
    }

    @Test
    public void testFloatAvg() {
        Errors err = new Errors();
        Expression e = Aggregate.createAvg(floatExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f / 5));

        e = (AvgAggregate) Aggregate.createAvg(floatExpr, new WindowSize(3),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3.3f));

        Expression filter = Comparison.createGT(floatExpr,
                Constant.create(2.2f, DataType.FLOAT), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createAvg(floatExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        res = e.run(null, floatView);
        assertThat(res, equalTo(7.7f / 2));
    }

    @Test
    public void testAvgNull() {
        Errors err = new Errors();
        Expression nulf = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createAvg(nulf, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createAvg(nulf, new WindowSize(3), nulb, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));

        e = Aggregate.createAvg(intExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testAvgBind() {
        Errors err = new Errors();
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c, err);
        assertTrue(err.isEmpty());

        Expression e = Aggregate.createAvg(exp, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createAvg(exp, new WindowSize(3), cFilt, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createAvg(exp, new WindowSize(3), filter, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createAvg(e, new WindowSize(3), filter, err);
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
    public void testIntegerMin() {
        Errors err = new Errors();
        Expression e = Aggregate.createMin(intExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(0));

        e = Aggregate.createMin(intExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(2));

        Expression filter = Comparison.createGT(intExpr,
                Constant.create(3, DataType.INTEGER), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createMin(intExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatMin() {
        Errors err = new Errors();
        Expression e = Aggregate.createMin(floatExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(0f));

        e = Aggregate.createMin(floatExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2.2f));

        Expression filter = Comparison.createGT(floatExpr,
                Constant.create(2.2f, DataType.FLOAT), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createMin(floatExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMin() {
        Errors err = new Errors();
        Expression e = Aggregate.createMin(tsExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(e.isComplete());
        Object res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(4)[0]));

        e = Aggregate.createMin(tsExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(2)[0]));
    }

    @Test
    public void testMinNull() {
        Errors err = new Errors();
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createMin(nuli, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertThat(res, nullValue());

        e = Aggregate.createMin(nuli, new WindowSize(3), nulb, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, nullValue());
    }

    @Test
    public void testMinBind() {
        Errors err = new Errors();
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c, err);
        assertTrue(err.isEmpty());

        Expression e = Aggregate.createMin(exp, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createMin(exp, new WindowSize(3), cFilt, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createMin(exp, new WindowSize(3), filter, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createMin(e, new WindowSize(3), filter, err);
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
    public void testIntegerMax() {
        Errors err = new Errors();
        Expression e = Aggregate.createMax(intExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        e = Aggregate.createMax(intExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        Expression filter = Comparison.createLT(intExpr,
                Constant.create(3, DataType.INTEGER), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createMax(intExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(2));
    }

    @Test
    public void testFloatMax() {
        Errors err = new Errors();
        Expression e = Aggregate.createMax(floatExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.FLOAT));
        assertTrue(e.isComplete());
        Object res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        e = Aggregate.createMax(floatExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        Expression filter = Comparison.createLT(floatExpr,
                Constant.create(4.4f, DataType.FLOAT), err);
        assertTrue(err.isEmpty());
        e = Aggregate.createMax(floatExpr, new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMax() {
        Errors err = new Errors();
        Expression e = Aggregate.createMax(tsExpr, new WindowSize(5),
                null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(e.isComplete());
        Object res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(0)[0]));

        e = Aggregate.createMax(tsExpr, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, tsView);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(tsView.get(0)[0]));
    }

    @Test
    public void testMaxNull() {
        Errors err = new Errors();
        Expression nulf = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createMax(nulf, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertThat(res, nullValue());

        e = Aggregate.createMax(nulf, new WindowSize(3), nulb, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, nullValue());
    }

    @Test
    public void testMaxBind() {
        Errors err = new Errors();
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression cFilt = Constant.TRUE;
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c, err);
        assertTrue(err.isEmpty());

        Expression e = Aggregate.createMax(exp, new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createMax(exp, new WindowSize(3), cFilt, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createMax(exp, new WindowSize(3), filter, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());

        e = Aggregate.createMax(e, new WindowSize(3), filter, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        bound.clear();
        e = e.bind(atts, bound, err);
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

    @Test
    public void testCount() {
        Errors err = new Errors();
        Expression e = Aggregate.createCount(new WindowSize(5), null, err);
        assertTrue(err.isEmpty());
        assertThat(e.getType(), equalTo(DataType.INTEGER));
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(5));

        e = Aggregate.createCount(new WindowSize(3), null, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, floatView);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(3));

        Expression filter = Comparison.createLT(intExpr,
                Constant.create(3, DataType.INTEGER), err);
        e = Aggregate.createCount(new WindowSize(5), filter, err);
        assertTrue(err.isEmpty());
        e = e.bind(atts, new ArrayList<>(), err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        res = e.run(null, intView);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testCountNull() {
        Errors err = new Errors();
        Expression nulb = Constant.NULL;

        Expression e = Aggregate.createCount(new WindowSize(3), nulb, err);
        assertTrue(err.isEmpty());
        assertTrue(e.isComplete());
        Object res = e.run(null, intView);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testCountBind() {
        Errors err = new Errors();
        Expression c = Constant.create(12, DataType.INTEGER);
        Expression exp = new Field("integer");
        Expression filter = Comparison.createGT(exp, c, err);
        assertTrue(err.isEmpty());

        Expression e = Aggregate.createCount(new WindowSize(3), filter, err);
        assertTrue(err.isEmpty());
        assertFalse(e.isComplete());
        List<Attribute> bound = new ArrayList<>();
        e = e.bind(atts, bound, err);
        assertTrue(err.isEmpty());
        assertThat(bound.size(), equalTo(1));
        assertTrue(bound.contains(intAtt));
        assertTrue(e.isComplete());
    }

}
