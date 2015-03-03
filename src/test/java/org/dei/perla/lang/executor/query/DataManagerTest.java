package org.dei.perla.lang.executor.query;

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
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 03/03/15.
 */
public class DataManagerTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static Expression tsExpr = new Field(0, DataType.TIMESTAMP);
    private static Expression intExpr = new Field(1, DataType.INTEGER);
    private static Expression stringExpr = new Field(2, DataType.STRING);
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

        Buffer b = new ArrayBuffer(atts, 512);
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:00.000Z"), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:15.000Z"), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:18.000Z"), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:25.000Z"), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{
                Instant.parse("2015-02-23T15:07:31.000Z"), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void plainSelect() throws InterruptedException {
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);

        DataManager dm = new DataManager(sel, 1, null, null, null, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(1);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);
        sel.add(new SumAggregate(intExpr, 3, null));

        DataManager dm = new DataManager(sel, 1, null, null, null, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(1);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);

        DataManager dm = new DataManager(sel, 3, null, null, null, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(3);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);

        DataManager dm = new DataManager(sel, -1,
                Duration.ofSeconds(10), null, null, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(2);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);
        sel.add(new SumAggregate(intExpr, 5, null));

        DataManager dm = new DataManager(sel, 3, null, null, null, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(3);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);

        Expression having = new NotEqual(intExpr,
                new Constant(3, DataType.INTEGER));

        DataManager dm = new DataManager(sel, 3, null, null, having, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(2);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);
        sel.add(new SumAggregate(intExpr, 5, null));

        Expression having = new NotEqual(intExpr,
                new Constant(3, DataType.INTEGER));

        DataManager dm = new DataManager(sel, 3, null, null, having, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(2);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

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
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);

        Expression having = new Equal(intExpr,
                new Constant(10, DataType.INTEGER));

        Object[] def = new Object[2];
        def[0] = Instant.now();
        def[1] = 5;

        DataManager dm = new DataManager(sel, 3, null, null, having, def);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(1);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

        assertThat(records.size(), equalTo(1));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(def[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(def[1]));
    }

}
