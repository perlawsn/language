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
public class SelectionTest {

    private static Attribute tsAtt = Attribute.TIMESTAMP;
    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static List<Attribute> atts;

    private static Expression tsExpr;
    private static Expression intExpr;
    private static Expression stringExpr;
    private static Expression floatExpr;

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                tsAtt,
                integerAtt,
                stringAtt,
                floatAtt
        };
        atts = Arrays.asList(as);

        tsExpr = new Field(tsAtt.getId()).rebuild(atts);
        intExpr = new Field(integerAtt.getId()).rebuild(atts);
        stringExpr = new Field(stringAtt.getId()).rebuild(atts);
        floatExpr = new Field(floatAtt.getId()).rebuild(atts);

        Buffer b = new ArrayBuffer(0, 512);
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

        Selection dm = new Selection(sel, null, null, null, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(1);
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
        sel.add(Aggregate.createSum(intExpr, new WindowSize(3), null));

        Selection dm = new Selection(sel, null, null, null, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(1);
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

        WindowSize upto = new WindowSize(3);
        Selection dm = new Selection(sel, upto, null, null, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(3);
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

        WindowSize upto = new WindowSize(Duration.ofSeconds(10));
        Selection dm = new Selection(sel, upto, null, null, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(2);
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
        sel.add(Aggregate.createSum(intExpr, new WindowSize(5), null));

        WindowSize upto = new WindowSize(3);
        Selection dm = new Selection(sel, upto, null, null, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(3);
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

        Expression having = Comparison.createNE(intExpr,
                new Constant(3, DataType.INTEGER));

        WindowSize upto = new WindowSize(3);
        Selection dm = new Selection(sel, upto, null, having, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(2);
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
        sel.add(Aggregate.createSum(intExpr, new WindowSize(5), null));

        Expression having = Comparison.createNE(intExpr,
                new Constant(3, DataType.INTEGER));

        WindowSize upto = new WindowSize(3);
        Selection dm = new Selection(sel, upto, null, having, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(2);
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

        Expression having = Comparison.createEQ(intExpr,
                new Constant(10, DataType.INTEGER));

        Object[] def = new Object[2];
        def[0] = Instant.now();
        def[1] = 5;

        WindowSize upto = new WindowSize(3);
        Selection dm = new Selection(sel, upto, null, having, def);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(1);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

        assertThat(records.size(), equalTo(1));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(def[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(def[1]));
    }

    @Test
    public void groupByTimestamp() throws InterruptedException {
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(new GroupTS());
        sel.add(intExpr);

        GroupBy group = new GroupBy(Duration.ofSeconds(1), 3);
        Selection dm = new Selection(sel, null, group, null, null);
        SynchronizerSelectHandler qh = new SynchronizerSelectHandler(3);
        dm.select(view, qh);
        List<Object[]> records = qh.getRecords();

        assertThat(records.size(), equalTo(3));
    }

}
