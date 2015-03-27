package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 03/03/15.
 */
public class SelectTest {

    private static Attribute tsAtt = Attribute.TIMESTAMP;
    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static final List<Attribute> atts;
    static {
        atts = Arrays.asList(new Attribute[] {
                Attribute.TIMESTAMP,
                integerAtt,
                stringAtt,
                floatAtt
        });
    }

    private static final Expression tsExpr = new Field(tsAtt.getId());
    private static final Expression intExpr = new Field(integerAtt.getId());
    private static final Expression stringExpr = new Field(stringAtt.getId());
    private static final Expression floatExpr = new Field(floatAtt.getId());

    private static BufferView createView(List<Attribute> atts) {
        Object[][] values = new Object[5][];
        values[0] = new Object[]{
                Instant.parse("2015-02-23T15:07:00.000Z"), 0, "0", 0.0f};
        values[1] = new Object[]{
                Instant.parse("2015-02-23T15:07:18.000Z"), 2, "2", 2.2f};
        values[2] = new Object[]{
                Instant.parse("2015-02-23T15:07:25.000Z"), 3, "3", 3.3f};
        values[3] = new Object[]{
                Instant.parse("2015-02-23T15:07:25.000Z"), 3, "3", 3.3f};
        values[4] = new Object[]{
                Instant.parse("2015-02-23T15:07:31.000Z"), 4, "4", 4.4f};

        int tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx == -1) {
            atts.add(Attribute.TIMESTAMP);
            tsIdx = atts.size() - 1;
        }

        Buffer b = new ArrayBuffer(tsIdx, 512);
        for (int i = 0; i < 5; i++) {
            Object[] row = new Object[atts.size()];
            int j = 0;
            for (Attribute a : atts) {
                switch (a.getType()) {
                    case TIMESTAMP:
                        row[j] = values[i][1];
                        break;
                    case INTEGER:
                        row[j] = values[i][2];
                        break;
                    case STRING:
                        row[j] = values[i][3];
                        break;
                    case FLOAT:
                        row[j] = values[i][3];
                        break;
                    default:
                        throw new RuntimeException(
                                "test does not support type " + a.getType());
                }
            }
            b.add(new Record(atts, row));
        }

        return b.unmodifiableView();
    }

    @Test
    public void plainSelect() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        Select sel = new Select(fields, null, null, null, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(1));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(0)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(0)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(0)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(0)[3]));
    }

    @Test
    public void aggregateSelect() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);
        fields.add(Aggregate.createSum(intExpr, new WindowSize(3), null));

        Select sel = new Select(fields, null, null, null, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(1));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(0)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(0)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(0)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(0)[3]));
        assertTrue(r[4] instanceof Integer);
        assertThat(r[4], equalTo(9));
    }

    @Test
    public void uptoSamples() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, null, null, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(3));
        for (int i = 0; i < records.size(); i++) {
            Object[] r = records.get(i);
            assertTrue(r[0] instanceof Instant);
            assertThat(r[0], equalTo(view.get(i)[0]));
            assertTrue(r[1] instanceof Integer);
            assertThat(r[1], equalTo(view.get(i)[1]));
            assertTrue(r[2] instanceof String);
            assertThat(r[2], equalTo(view.get(i)[2]));
            assertTrue(r[3] instanceof Float);
            assertThat(r[3], equalTo(view.get(i)[3]));
        }
    }

    @Test
    public void uptoDuration() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        WindowSize upto = new WindowSize(Duration.ofSeconds(10));
        Select sel = new Select(fields, upto, null, null, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(2));
        for (int i = 0; i < records.size(); i++) {
            Object[] r = records.get(i);
            assertTrue(r[0] instanceof Instant);
            assertThat(r[0], equalTo(view.get(i)[0]));
            assertTrue(r[1] instanceof Integer);
            assertThat(r[1], equalTo(view.get(i)[1]));
            assertTrue(r[2] instanceof String);
            assertThat(r[2], equalTo(view.get(i)[2]));
            assertTrue(r[3] instanceof Float);
            assertThat(r[3], equalTo(view.get(i)[3]));
        }
    }

    @Test
    public void uptoAggregate() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);
        fields.add(Aggregate.createSum(intExpr, new WindowSize(5), null));

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, null, null, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(3));
        for (int i = 0; i < records.size(); i++) {
            Object[] r = records.get(i);
            assertTrue(r[0] instanceof Instant);
            assertThat(r[0], equalTo(view.get(i)[0]));
            assertTrue(r[1] instanceof Integer);
            assertThat(r[1], equalTo(view.get(i)[1]));
            assertTrue(r[2] instanceof String);
            assertThat(r[2], equalTo(view.get(i)[2]));
            assertTrue(r[3] instanceof Float);
            assertThat(r[3], equalTo(view.get(i)[3]));
            assertTrue(r[4] instanceof Integer);
            assertThat(r[4], equalTo(10));
        }
    }

    @Test
    public void having() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        Expression having = Comparison.createNE(intExpr,
                Constant.create(3, DataType.INTEGER));

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, null, having, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(2));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(0)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(0)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(0)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(0)[3]));

        r = records.get(1);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(2)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(2)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(2)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(2)[3]));
    }

    @Test
    public void havingAggregate() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);
        fields.add(Aggregate.createSum(intExpr, new WindowSize(5), null));

        Expression having = Comparison.createNE(intExpr,
                Constant.create(3, DataType.INTEGER));

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, null, having, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(2));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(0)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(0)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(0)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(0)[3]));
        assertTrue(r[4] instanceof Integer);
        assertThat(r[4], equalTo(10));

        r = records.get(1);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(2)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(2)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(2)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(2)[3]));
        assertTrue(r[4] instanceof Integer);
        assertThat(r[4], equalTo(10));
    }

    @Test
    public void insertOnEmpty() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);

        Expression having = Comparison.createEQ(intExpr,
                Constant.create(10, DataType.INTEGER));

        Object[] def = new Object[2];
        def[0] = Instant.now();
        def[1] = 5;

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, null, having, def);
        List<Attribute> bound = new ArrayList<>();
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(1));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(def[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(def[1]));
    }

    @Test
    public void groupByTimestamp() throws InterruptedException {
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(new GroupTS());
        fields.add(intExpr);

        GroupBy group = new GroupBy(Duration.ofSeconds(1), 3);
        Select sel = new Select(fields, null, group, null, null);
        List<Attribute> bound = new ArrayList<>();
        sel = sel.bind(atts, bound);
        BufferView view = createView(bound);
        List<Object[]> records = sel.select(view);

        assertThat(records.size(), equalTo(3));
    }

}
