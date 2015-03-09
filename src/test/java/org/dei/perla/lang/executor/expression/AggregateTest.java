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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 02/03/15.
 */
public class AggregateTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static Expression tsExpr = new Field(0, DataType.TIMESTAMP);
    private static Expression intExpr = new Field(1, DataType.INTEGER);
    private static Expression floatExpr = new Field(3, DataType.FLOAT);

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
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
    public void testIntegerSum() {
        SumAggregate sum = new SumAggregate(intExpr, new WindowSize(5), null);
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        Object res = sum.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(10));

        sum = new SumAggregate(intExpr, new WindowSize(3), null);
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        res = sum.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(9));

        Expression where = new Greater(new Field(3, DataType.FLOAT),
                new Constant(3f, DataType.FLOAT));
        sum = new SumAggregate(intExpr, new WindowSize(5), where);
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        res = sum.run(null, view);
        assertThat(res, equalTo(7));
    }

    @Test
    public void testFloatSum() {
        SumAggregate sum = new SumAggregate(floatExpr, new WindowSize(5), null);
        Object res = sum.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f));

        sum = new SumAggregate(floatExpr, new WindowSize(3), null);
        res = sum.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(9.9f));

        Expression where = new Greater(new Field(1, DataType.INTEGER),
                new Constant(2, DataType.INTEGER));
        sum = new SumAggregate(floatExpr, new WindowSize(5), where);
        res = sum.run(null, view);
        assertThat(res, equalTo(7.7f));
    }

    @Test
    public void testIntegerAvg() {
        AvgAggregate avg = new AvgAggregate(intExpr, new WindowSize(5), null);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        Object res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2f));

        avg = new AvgAggregate(intExpr, new WindowSize(3), null);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3f));

        Expression where = new Greater(new Field(3, DataType.FLOAT),
                new Constant(3f, DataType.FLOAT));
        avg = new AvgAggregate(intExpr, new WindowSize(5), where);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        res = avg.run(null, view);
        assertThat(res, equalTo(7f / 2));
    }

    @Test
    public void testFloatAvg() {
        AvgAggregate avg = new AvgAggregate(floatExpr, new WindowSize(5), null);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        Object res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f / 5));

        avg = new AvgAggregate(floatExpr, new WindowSize(3), null);
        res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3.3f));

        Expression where = new Greater(new Field(1, DataType.INTEGER),
                new Constant(2, DataType.INTEGER));
        avg = new AvgAggregate(floatExpr, new WindowSize(5), where);
        res = avg.run(null, view);
        assertThat(res, equalTo(7.7f / 2));
    }

    @Test
    public void testIntegerMin() {
        MinAggregate min = new MinAggregate(intExpr, new WindowSize(5), null);
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        Object res = min.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(0));

        min = new MinAggregate(intExpr, new WindowSize(3), null);
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        res = min.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(2));

        Expression where = new Greater(new Field(3, DataType.FLOAT),
                new Constant(3f, DataType.FLOAT));
        min = new MinAggregate(intExpr, new WindowSize(5), where);
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        res = min.run(null, view);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testFloatMin() {
        MinAggregate min = new MinAggregate(floatExpr, new WindowSize(5), null);
        assertThat(min.getType(), equalTo(DataType.FLOAT));
        Object res = min.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(0f));

        min = new MinAggregate(floatExpr, new WindowSize(3), null);
        res = min.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2.2f));

        Expression where = new Greater(new Field(1, DataType.INTEGER),
                new Constant(2, DataType.INTEGER));
        min = new MinAggregate(floatExpr, new WindowSize(5), where);
        res = min.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testIntegerMax() {
        MaxAggregate max = new MaxAggregate(intExpr, new WindowSize(5), null);
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        Object res = max.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        max = new MaxAggregate(intExpr, new WindowSize(3), null);
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        res = max.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        Expression where = new Less(new Field(3, DataType.FLOAT),
                new Constant(3f, DataType.FLOAT));
        max = new MaxAggregate(intExpr, new WindowSize(5), where);
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        res = max.run(null, view);
        assertThat(res, equalTo(2));
    }

    @Test
    public void testFloatMax() {
        MaxAggregate max = new MaxAggregate(floatExpr, new WindowSize(5), null);
        assertThat(max.getType(), equalTo(DataType.FLOAT));
        Object res = max.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        max = new MaxAggregate(floatExpr, new WindowSize(3), null);
        res = max.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        Expression filter = new Less(new Field(1, DataType.INTEGER),
                new Constant(4, DataType.INTEGER));
        max = new MaxAggregate(floatExpr, new WindowSize(5), filter);
        res = max.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMax() {
        MaxAggregate max = new MaxAggregate(tsExpr, new WindowSize(5), null);
        assertThat(max.getType(), equalTo(DataType.TIMESTAMP));
        Object res = max.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(0)[0]));

        max = new MaxAggregate(tsExpr, new WindowSize(3), null);
        res = max.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(0)[0]));

        Expression filter = new Less(new Field(1, DataType.INTEGER),
                new Constant(4, DataType.INTEGER));
        max = new MaxAggregate(floatExpr, new WindowSize(5), filter);
        res = max.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testCount() {
        CountAggregate count = new CountAggregate(new WindowSize(5), null);
        assertThat(count.getType(), equalTo(DataType.INTEGER));
        Object res = count.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(5));

        count = new CountAggregate(new WindowSize(3), null);
        res = count.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(3));

        Expression where = new Less(new Field(1, DataType.INTEGER),
                new Constant(3, DataType.INTEGER));
        count = new CountAggregate(new WindowSize(5), where);
        res = count.run(null, view);
        assertThat(res, equalTo(3));
    }

}
