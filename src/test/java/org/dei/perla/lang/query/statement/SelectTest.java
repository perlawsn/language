package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.buffer.ArrayBuffer;
import org.dei.perla.lang.executor.buffer.Buffer;
import org.dei.perla.lang.executor.buffer.BufferView;
import org.dei.perla.lang.query.expression.*;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 03/03/15.
 */
public class SelectTest {
/*
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

    private static final BufferView view;
    static {
        Buffer b = new ArrayBuffer(atts, 512);
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:00.000Z"), 0, "0", 0.0f});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:15.000Z"), 1, "1", 1.1f});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:18.000Z"), 2, "2", 2.2f});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:25.000Z"), 3, "3", 3.3f});
        b.add(new Object[]{
                Instant.parse("2015-02-23T15:07:31.000Z"), 4, "4", 4.4f});

        view = b.unmodifiableView();
    }

    @Test
    public void plainSelect() throws InterruptedException {
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        Select sel = new Select(fields, WindowSize.ONE, GroupBy.NONE,
                Constant.TRUE, new Object[0]);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(1));
        Object[] r = samples.get(0);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);
        fields.add(Aggregate.createSum(intExpr, new WindowSize(3),
                Constant.TRUE, err));
        assertTrue(err.isEmpty());

        Select sel = new Select(fields, WindowSize.ONE, GroupBy.NONE, Constant.TRUE,
                new Object[0]);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(1));
        Object[] r = samples.get(0);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, GroupBy.NONE, Constant.TRUE,
                new Object[0]);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(3));
        for (int i = 0; i < samples.size(); i++) {
            Object[] r = samples.get(i);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        WindowSize upto = new WindowSize(Duration.ofSeconds(10));
        Select sel = new Select(fields, upto, GroupBy.NONE, Constant.TRUE,
                new Object[0]);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(2));
        for (int i = 0; i < samples.size(); i++) {
            Object[] r = samples.get(i);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);
        fields.add(Aggregate.createSum(intExpr, new WindowSize(5),
                Constant.TRUE, err));

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, GroupBy.NONE, Constant.TRUE,
                new Object[0]);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(3));
        for (int i = 0; i < samples.size(); i++) {
            Object[] r = samples.get(i);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);

        Expression having = Comparison.createNE(intExpr,
                Constant.create(3, DataType.INTEGER), err);

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, GroupBy.NONE, having, new Object[0]);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(2));
        Object[] r = samples.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(0)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(0)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(0)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(0)[3]));

        r = samples.get(1);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);
        fields.add(stringExpr);
        fields.add(floatExpr);
        fields.add(Aggregate.createSum(intExpr, new WindowSize(5),
                Constant.TRUE, err));
        assertTrue(err.isEmpty());

        Expression having = Comparison.createNE(intExpr,
                Constant.create(3, DataType.INTEGER), err);
        assertTrue(err.isEmpty());

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, GroupBy.NONE, having,
                new Object[0]);
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(2));
        Object[] r = samples.get(0);
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

        r = samples.get(1);
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
        Errors err = new Errors();
        List<Expression> fields = new ArrayList<>();
        fields.add(tsExpr);
        fields.add(intExpr);

        Expression having = Comparison.createEQ(intExpr,
                Constant.create(10, DataType.INTEGER), err);
        assertTrue(err.isEmpty());

        Object[] def = new Object[2];
        def[0] = Instant.now();
        def[1] = 5;

        WindowSize upto = new WindowSize(3);
        Select sel = new Select(fields, upto, GroupBy.NONE, having, def);
        assertFalse(sel.isComplete());
        sel = sel.bind(atts, atts, err);
        assertTrue(err.isEmpty());
        assertTrue(sel.isComplete());
        List<Object[]> samples = sel.select(view);

        assertThat(samples.size(), equalTo(1));
        Object[] r = samples.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(def[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(def[1]));
    }
*/
}
