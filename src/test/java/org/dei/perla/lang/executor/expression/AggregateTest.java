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

    private static Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

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
        List<Attribute> atts = Arrays.asList(as);

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
        SumAggregate sum = (SumAggregate) Aggregate.createSum(intExpr, new
                WindowSize(5), null);
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        assertTrue(sum.isComplete());
        Object res = sum.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(10));

        sum = (SumAggregate) Aggregate.createSum (intExpr, new WindowSize(3),
                null);
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        assertTrue(sum.isComplete());
        res = sum.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(9));

        Expression filter = Comparison.createGT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        sum = (SumAggregate) Aggregate.createSum(intExpr, new WindowSize(5),
                filter);
        assertThat(sum.getType(), equalTo(DataType.INTEGER));
        assertTrue(sum.isComplete());
        res = sum.run(null, view);
        assertThat(res, equalTo(7));
    }

    @Test
    public void testFloatSum() {
        SumAggregate sum = (SumAggregate) Aggregate.createSum(floatExpr, new
                WindowSize(5), null);
        assertThat(sum.getType(), equalTo(DataType.FLOAT));
        assertTrue(sum.isComplete());
        Object res = sum.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f));

        sum = (SumAggregate) Aggregate.createSum(floatExpr, new WindowSize(3),
                null);
        assertThat(sum.getType(), equalTo(DataType.FLOAT));
        assertTrue(sum.isComplete());
        res = sum.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(9.9f));

        Expression filter = Comparison.createGT(intExpr,
                new Constant(2, DataType.INTEGER));
        sum = (SumAggregate) Aggregate.createSum(floatExpr, new WindowSize(5),
                filter);
        assertThat(sum.getType(), equalTo(DataType.FLOAT));
        assertTrue(sum.isComplete());
        res = sum.run(null, view);
        assertThat(res, equalTo(7.7f));
    }

    @Test
    public void testIntegerAvg() {
        AvgAggregate avg = (AvgAggregate) Aggregate.createAvg(intExpr, new WindowSize(5), null);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        assertTrue(avg.isComplete());
        Object res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2f));

        avg = (AvgAggregate) Aggregate.createAvg(intExpr, new WindowSize(3), null);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        assertTrue(avg.isComplete());
        res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3f));

        Expression filter = Comparison.createGT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        avg = (AvgAggregate) Aggregate.createAvg(intExpr, new WindowSize(5), filter);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        assertTrue(avg.isComplete());
        res = avg.run(null, view);
        assertThat(res, equalTo(7f / 2));
    }

    @Test
    public void testFloatAvg() {
        AvgAggregate avg = (AvgAggregate) Aggregate.createAvg(floatExpr, new WindowSize(5), null);
        assertThat(avg.getType(), equalTo(DataType.FLOAT));
        assertTrue(avg.isComplete());
        Object res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(11f / 5));

        avg = (AvgAggregate) Aggregate.createAvg(floatExpr, new WindowSize(3), null);
        res = avg.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(3.3f));

        Expression filter = Comparison.createGT(intExpr,
                new Constant(2, DataType.INTEGER));
        avg = (AvgAggregate) Aggregate.createAvg(floatExpr, new WindowSize(5), filter);
        res = avg.run(null, view);
        assertThat(res, equalTo(7.7f / 2));
    }

    @Test
    public void testIntegerMin() {
        MinAggregate min = (MinAggregate) Aggregate.createMin(intExpr, new WindowSize(5), null);
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        assertTrue(min.isComplete());
        Object res = min.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(0));

        min = (MinAggregate) Aggregate.createMin(intExpr, new WindowSize(3), null);
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        assertTrue(min.isComplete());
        res = min.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(2));

        Expression filter = Comparison.createGT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        min = (MinAggregate) Aggregate.createMin(intExpr, new WindowSize(5), filter);
        assertThat(min.getType(), equalTo(DataType.INTEGER));
        assertTrue(min.isComplete());
        res = min.run(null, view);
        assertThat(res, equalTo(3));
    }

    @Test
    public void testFloatMin() {
        MinAggregate min = (MinAggregate) Aggregate.createMin(floatExpr, new WindowSize(5), null);
        assertThat(min.getType(), equalTo(DataType.FLOAT));
        assertTrue(min.isComplete());
        Object res = min.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(0f));

        min = (MinAggregate) Aggregate.createMin(floatExpr, new WindowSize(3), null);
        assertTrue(min.isComplete());
        res = min.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(2.2f));

        Expression filter = Comparison.createGT(intExpr,
                new Constant(2, DataType.INTEGER));
        min = (MinAggregate) Aggregate.createMin(floatExpr, new WindowSize(5), filter);
        assertTrue(min.isComplete());
        res = min.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMin() {
        MinAggregate min = (MinAggregate) Aggregate.createMin(tsExpr, new WindowSize(5), null);
        assertThat(min.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(min.isComplete());
        Object res = min.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(4)[0]));

        min = (MinAggregate) Aggregate.createMin(tsExpr, new WindowSize(3), null);
        assertTrue(min.isComplete());
        res = min.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(2)[0]));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(4, DataType.INTEGER));
        min = (MinAggregate) Aggregate.createMin(tsExpr, new WindowSize(5), filter);
        assertTrue(min.isComplete());
        res = min.run(null, view);
        assertThat(res, equalTo(view.get(4)[0]));
    }

    @Test
    public void testIntegerMax() {
        MaxAggregate max = (MaxAggregate) Aggregate.createMax(intExpr, new WindowSize(5), null);
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        assertTrue(max.isComplete());
        Object res = max.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        max = (MaxAggregate) Aggregate.createMax(intExpr, new WindowSize(3), null);
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        assertTrue(max.isComplete());
        res = max.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(4));

        Expression filter = Comparison.createLT(floatExpr,
                new Constant(3f, DataType.FLOAT));
        max = (MaxAggregate) Aggregate.createMax(intExpr, new WindowSize(5), filter);
        assertThat(max.getType(), equalTo(DataType.INTEGER));
        assertTrue(max.isComplete());
        res = max.run(null, view);
        assertThat(res, equalTo(2));
    }

    @Test
    public void testFloatMax() {
        MaxAggregate max = (MaxAggregate) Aggregate.createMax(floatExpr, new WindowSize(5), null);
        assertThat(max.getType(), equalTo(DataType.FLOAT));
        assertTrue(max.isComplete());
        Object res = max.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        max = (MaxAggregate) Aggregate.createMax(floatExpr, new WindowSize(3), null);
        assertTrue(max.isComplete());
        res = max.run(null, view);
        assertTrue(res instanceof Float);
        assertThat(res, equalTo(4.4f));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(4, DataType.INTEGER));
        max = (MaxAggregate) Aggregate.createMax(floatExpr, new WindowSize(5), filter);
        assertTrue(max.isComplete());
        res = max.run(null, view);
        assertThat(res, equalTo(3.3f));
    }

    @Test
    public void testInstantMax() {
        MaxAggregate max = (MaxAggregate) Aggregate.createMax(tsExpr, new WindowSize(5), null);
        assertThat(max.getType(), equalTo(DataType.TIMESTAMP));
        assertTrue(max.isComplete());
        Object res = max.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(0)[0]));

        max = (MaxAggregate) Aggregate.createMax(tsExpr, new WindowSize(3), null);
        assertTrue(max.isComplete());
        res = max.run(null, view);
        assertTrue(res instanceof Instant);
        assertThat(res, equalTo(view.get(0)[0]));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(4, DataType.INTEGER));
        max = (MaxAggregate) Aggregate.createMax(tsExpr, new WindowSize(5), filter);
        assertTrue(max.isComplete());
        res = max.run(null, view);
        assertThat(res, equalTo(view.get(1)[0]));
    }

    @Test
    public void testCount() {
        CountAggregate count = (CountAggregate) Aggregate.createCount(new WindowSize(5), null);
        assertThat(count.getType(), equalTo(DataType.INTEGER));
        assertTrue(count.isComplete());
        Object res = count.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(5));

        count = (CountAggregate) Aggregate.createCount(new WindowSize(3), null);
        assertTrue(count.isComplete());
        res = count.run(null, view);
        assertTrue(res instanceof Integer);
        assertThat(res, equalTo(3));

        Expression filter = Comparison.createLT(intExpr,
                new Constant(3, DataType.INTEGER));
        count = (CountAggregate) Aggregate.createCount(new WindowSize(5), filter);
        assertTrue(count.isComplete());
        res = count.run(null, view);
        assertThat(res, equalTo(3));
    }

}
