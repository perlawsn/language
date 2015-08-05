package org.dei.perla.lang.query.expression;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.statement.WindowSize;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
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
            new Field("integer", DataType.INTEGER, 0);
    private static final Expression intExpr =
            new Field("string", DataType.STRING, 1);
    private static final Expression floatExpr =
            new Field("float", DataType.FLOAT, 2);

    private static BufferView view;

    @BeforeClass
    public static void setupBuffer() {
        List<Attribute> bufAtts = Arrays.asList(new Attribute[]{
                intAtt,
                Attribute.TIMESTAMP
        });
        Buffer b = new ArrayBuffer(bufAtts, 512);
        b.add(new Object[]{0, "test", 0.0f, Instant.now()});
        b.add(new Object[]{1, "test", 1.1f, Instant.now()});
        b.add(new Object[]{2, "test", 2.2f, Instant.now()});
        b.add(new Object[]{3, "test", 3.3f, Instant.now()});
        b.add(new Object[]{4, "test", 4.4f, Instant.now()});
        view = b.unmodifiableView();
    }

    @Test
    public void testIntegerSum() {
        Expression e = new SumAggregate(intExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(10));

        e = new SumAggregate(intExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo(9));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3, DataType.INTEGER));
        e = new SumAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatSum() {
        Expression e = new SumAggregate(floatExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(11f));

        e = new SumAggregate(floatExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo(9.9f));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3.3f, DataType.FLOAT));
        e = new SumAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4.4f));
    }

    @Test
    public void testSumNull() {
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = new SumAggregate(nuli, new WindowSize(3), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new SumAggregate(intExpr, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new SumAggregate(nuli, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testIntegerAvg() {
        Expression e = new AvgAggregate(intExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo((0 + 1 + 2 + 3 + 4) / 5));

        e = new AvgAggregate(intExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo((0 + 1 + 2) / 3));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3, DataType.INTEGER));
        e = new AvgAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatAvg() {
        Expression e = new AvgAggregate(floatExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo((0.0f + 1.1f + 2.2f + 3.3f + 4.4f)/5));

        e = new AvgAggregate(floatExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo((0.0f + 1.1f + 2.2f) / 3));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3.3f, DataType.FLOAT));
        e = new AvgAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4.4f));
    }

    @Test
    public void testAvgNull() {
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = new AvgAggregate(nuli, new WindowSize(3), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new AvgAggregate(intExpr, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new AvgAggregate(nuli, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testIntegerMin() {
        Expression e = new MinAggregate(intExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new MinAggregate(intExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo(0));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3, DataType.INTEGER));
        e = new MinAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatMin() {
        Expression e = new MinAggregate(floatExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0.0f));

        e = new MinAggregate(floatExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo(0.0f));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3.3f, DataType.FLOAT));
        e = new MinAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4.4f));
    }

    @Test
    public void testMinNull() {
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = new MinAggregate(nuli, new WindowSize(3), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new MinAggregate(intExpr, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new MinAggregate(nuli, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testIntegerMax() {
        Expression e = new MaxAggregate(intExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(4));

        e = new MaxAggregate(intExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo(2));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3, DataType.INTEGER));
        e = new MaxAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4));
    }

    @Test
    public void testFloatMax() {
        Expression e = new MaxAggregate(floatExpr,
                new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(4.4f));

        e = new MaxAggregate(floatExpr, new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertThat(res, equalTo(2.2f));

        Expression filter = new Comparison(ComparisonOperation.GT, intExpr,
                Constant.create(3.3f, DataType.FLOAT));
        e = new MaxAggregate(intExpr, new WindowSize(3), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(4.4f));
    }

    @Test
    public void testMaxNull() {
        Expression nuli = Constant.NULL;
        Expression nulb = Constant.NULL;

        Expression e = new MaxAggregate(nuli, new WindowSize(3), Constant.TRUE);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new MaxAggregate(intExpr, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));

        e = new MaxAggregate(nuli, new WindowSize(3), nulb);
        res = e.run(null, view);
        assertThat(res, equalTo(0));
    }

    @Test
    public void testCount() {
        Expression e = new CountAggregate(new WindowSize(5), Constant.TRUE);
        Object res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(5));

        e = new CountAggregate(new WindowSize(3), Constant.TRUE);
        res = e.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(3));

        Expression filter = new Comparison(ComparisonOperation.LT, intExpr,
                Constant.create(3, DataType.INTEGER));
        e = new CountAggregate(new WindowSize(5), filter);
        res = e.run(null, view);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testCountNull() {
        Expression nulb = Constant.NULL;

        Expression e = new CountAggregate(new WindowSize(3), nulb);
        Object res = e.run(null, view);
        assertThat(res, equalTo(0));
    }

}
